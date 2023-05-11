import static io.restassured.RestAssured.*;

import io.restassured.parsing.Parser;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

import io.restassured.path.json.JsonPath;
import org.openqa.selenium.chrome.ChromeDriver;
import pojo.GetCourse;

public class oAuthTest {

    public static void main(String[] args) throws InterruptedException {
        //    System.setProperty("webdriver.chrome.driver", "/Users/vjaceslavsjermakovs/IdeaProjects/chrome driver/chromedriver113");
        //    WebDriver driver = new ChromeDriver();
        //    driver.get("https://accounts.google.com/o/oauth2/v2/auth?scope=https://www.googleapis.com/auth/userinfo.email&auth_url=https://accounts.google.com/o/oauth2/v2/auth&client_id=692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com&response_type=code&redirect_uri=https://rahulshettyacademy.com/getCourse.php");
        //    driver.findElement(By.cssSelector("input[type='email']")).sendKeys("test.udemy14");
        //    driver.findElement (By.cssSelector("input [type='email']")).sendKeys (Keys. ENTER);
        //    Thread.sleep (3000);
        //    driver.findElement(By.cssSelector("input [type='password']")).sendKeys ("Password4512");
        //    driver.findElement (By.cssSelector("input [type='password']")). sendKeys (Keys. ENTER);
        //    Thread.sleep (4000);
        //    String url = driver.getCurrentUrl();
        String url = "https://rahulshettyacademy.com/getCourse.php?code=4%2F0AbUR2VOFGDiRaQYhVnVUNZIegQTo11DihDeMxKoXyyft81jTlKrPKShrLHhABRdnxHmVvQ&scope=email+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+openid&authuser=0&prompt=none";
        String partialCode = url.split("code=")[1];
        String code = partialCode.split("&scope")[0];
        System.out.println(code);
        String accessTokenResponse =
                given()
                        .urlEncodingEnabled(false)
                        .queryParams("code", code)
                        .queryParams("client_id", "692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com")
                        .queryParams("client_secret", "erZOWM9g3UtwNRj340YYaK_W")
                        .queryParams("grant_type", "authorization_code")
                        .queryParams("redirect_uri", "https://rahulshettyacademy.com/getCourse.php")
                        .when().log().all()
                        .post("https://www.googleapis.com/oauth2/v4/token").asString();
        JsonPath js = new JsonPath(accessTokenResponse);
        String accessToken = js.getString("access_token");



        GetCourse gs =
                given().queryParam("access_token", accessToken).expect().defaultParser(Parser.JSON)
                        .when()
                        .get("https://rahulshettyacademy.com/getCourse.php").as(GetCourse.class);

        System.out.println(gs.getLinkedIn());
        System.out.println(gs.getInstructor());

        //System.out.println(response);


    }
}
