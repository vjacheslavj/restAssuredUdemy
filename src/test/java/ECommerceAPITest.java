import com.google.gson.JsonArray;
import com.sun.org.apache.xpath.internal.operations.Or;
import files.ReUsableMethods;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import pojo.LoginRequest;
import pojo.LoginResponse;
import pojo.OrderDetail;
import pojo.Orders;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

public class ECommerceAPITest {

    public static void main(String[] args) {
        //SSL
        RequestSpecification req = new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
                .setContentType(ContentType.JSON).build();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserEmail("vjacheslav.jermakov@gmail.com");
        loginRequest.setUserPassword("Word626812!");

        RequestSpecification reqLogin = given().relaxedHTTPSValidation().log().all().spec(req).body(loginRequest);
        LoginResponse loginResponse = reqLogin.when().post("/api/ecom/auth/login").then().log().all().extract().response()
                .as(LoginResponse.class);
        System.out.println(loginResponse.getToken());
        String token = loginResponse.getToken();
        System.out.println(loginResponse.getUserId());
        String userId = loginResponse.getUserId();

        //Add Product

        RequestSpecification addProductBaseReq = new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
                .addHeader("authorization", token)
                .build();

        RequestSpecification reqAddProduct = given().log().all().spec(addProductBaseReq)
                .param("productName", "Laptop")
                .param("productAddedBy", userId)
                .param("productCategory", "fashion")
                .param("productSubCategory", "shirts")
                .param("productPrice", "11500")
                .param("productDescription", "Addidas Originals")
                .param("productFor", "women")
                .multiPart("productImage", new File("/Users/vjaceslavsjermakovs/Desktop/123.jpeg"));

        String addProductResponse = reqAddProduct.when().post("/api/ecom/product/add-product")
                .then().log().all().extract().response().asString();
        JsonPath js = new JsonPath(addProductResponse);
        String productId = js.get("productId");

        //Create order

        RequestSpecification createOrderBaseReq = new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
                .addHeader("authorization", token).setContentType(ContentType.JSON)
                .build();
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setCountry("Latvia");
        orderDetail.setProductOrderedId(productId);

        List<OrderDetail> orderDetailList = new ArrayList<OrderDetail>();
        orderDetailList.add(orderDetail);
        Orders orders = new Orders();
        orders.setOrders(orderDetailList);

        RequestSpecification createOrderReq = given().log().all().spec(createOrderBaseReq).body(orders);

        String responseAddOrder = createOrderReq.when().post("/api/ecom/order/create-order").then().log().all()
                .extract().response().asString();
        JsonPath js3= ReUsableMethods.rawToJson(responseAddOrder);
       // JsonPath js3 = new JsonPath(responseAddOrder);
        String orderIdWithBrackets = js3.getString("orders");
        String orderId = orderIdWithBrackets.replaceAll("[\\[\\](){}]", "");

        System.out.println(orderId);
        System.out.println(responseAddOrder);

        //Delete Product

        RequestSpecification deleteProductBaseReq = new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
                .addHeader("authorization", token).setContentType(ContentType.JSON)
                .build();

        RequestSpecification deleteProdReq = given().log().all().spec(deleteProductBaseReq).pathParams("productId", productId);

        String deleteProductResponse = deleteProdReq.when().delete("/api/ecom/product/delete-product/{productId}").then().log().all()
                .extract().response().asString();

        JsonPath js1 = new JsonPath(deleteProductResponse);
        Assert.assertEquals("Product Deleted Successfully", js1.get("message"));

        //Delete Order

        RequestSpecification deleteOrderBaseReq = new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
                .addHeader("authorization", token).setContentType(ContentType.JSON)
                .build();

        RequestSpecification deleteOrderReq = given().log().all().spec(deleteOrderBaseReq)
                .pathParams("orderId", orderId);
        System.out.println(orderId);

        String deleteOrderResponse = deleteOrderReq.when().delete("/api/ecom/order/delete-order/{orderId}").then().log().all()
                .extract().response().asString();

        JsonPath js2 = new JsonPath(deleteOrderResponse);
        Assert.assertEquals("Orders Deleted Successfully", js2.get("message"));
    }
}
