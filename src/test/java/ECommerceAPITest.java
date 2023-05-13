import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import pojo.LoginRequest;
import pojo.LoginResponse;

import static io.restassured.RestAssured.given;

public class ECommerceAPITest {

    public static void main (String[] args) {

        RequestSpecification req = new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
                .setContentType(ContentType.JSON).build();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserEmail("vjacheslav.jermakov@gmail.com");
        loginRequest.setUserPassword("Word626812!");

        RequestSpecification reqLogin = given().log().all().spec(req).body(loginRequest);
        LoginResponse loginResponse = reqLogin.when().post("/api/ecom/auth/login").then().log().all().extract().response()
                .as(LoginResponse.class);
        System.out.println(loginResponse.getToken());
        System.out.println(loginResponse.getUserId());
    }
}
