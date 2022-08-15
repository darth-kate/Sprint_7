import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.*;
import io.qameta.allure.Step;

import java.util.Locale;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class PostCourierLoginTest {
        private final static Random random = new Random();
        private static String login;
        private static String password;

        @Before
        public void setUp() {
            RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/api/v1";
            login = "test_courier_" + random.nextInt(1000);
            password = "testpassword";
        }

    @Step("Create courier")
    public void createCourier(String route, String login, String password, String firstName){
        PostCourierSerial courier  = new PostCourierSerial(login, password, firstName);
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post(route);
    }

        @Step("Send {methodName} request to {route}")
        public Response sendRequest(String route, String login, String password, String methodName) throws RuntimeException {
            LoginCourierSerial courier  = new LoginCourierSerial(login, password);
            RequestSpecification spec = given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(courier)
                    .when();

            Response response;
            switch (methodName.toUpperCase(Locale.ROOT)) {
                case "POST":
                    response = spec.post(route);
                    break;
                default:
                    throw new RuntimeException("Unsupported HTTP method");
            }

            return response;
        }

        @Step("Compare id")
        public void compareId(Response response){
            response.then().assertThat().body("id", any(Integer.class));
        }

        @Step("Compare code")
        public void compareCodeField(Response response, int code){
            response.then().assertThat().body("code", equalTo(code));
        }

        @Step("Compare message")
        public void compareMessage(Response response, String message){
            response.then().assertThat().body("message", equalTo(message));
        }

        @Step("Assert status code of response")
        public void assertStatusCode(Response response, int statusCode){
            response.then().assertThat().statusCode(statusCode);
        }

        @Test
        @DisplayName("POST /courier/login log in courier with all fields successfully")
        public void validBodyTest() {
            createCourier("/courier", login, password, "test_courier");
            Response response = sendRequest("/courier/login", login, password, "POST");
            compareId(response);
            assertStatusCode(response, 200);
        }

    @Test
    @DisplayName("POST /courier/login don't log in courier without login field")
    public void loginWithoutLoginTest() {
        createCourier("/courier", login, password, "test_courier");
            Response response = sendRequest("/courier/login", "", password, "POST");
            compareCodeField(response, 400);
            compareMessage(response, "Недостаточно данных для входа");
            assertStatusCode(response, 400);
    }

    @Test
    @DisplayName("POST /courier/login don't log in courier without password field")
    public void loginWithoutPasswordTest() {
        createCourier("/courier", login, password, "test_courier");
            Response response = sendRequest("/courier/login", login, "", "POST");
            compareCodeField(response, 400);
            compareMessage(response, "Недостаточно данных для входа");
            assertStatusCode(response, 400);
    }

    @Test
    @DisplayName("POST /courier/login don't log in courier with non-existent values")
    public void loginWithFakeCourierTest() {
            login = String.valueOf(random.nextInt(100));
            password = String.valueOf(random.nextInt(100));
            Response response = sendRequest("/courier/login", login, password, "POST");
            compareCodeField(response, 404);
            compareMessage(response, "Учетная запись не найдена");
            assertStatusCode(response, 404);

    }

        @After
        public void coolDown () {
            if(!login.contains("test_courier_")){
                return;
            }

            LoginCourierSerial courier = new LoginCourierSerial(login, password);
            int courierId = given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(courier)
                    .when()
                    .post("/courier/login")
                    .then()
                    .extract()
                    .body()
                    .path("id");
            given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(courier)
                    .when()
                    .delete("/courier/{courierId}", courierId)
                    .then().assertThat().statusCode(200);

        }


}
