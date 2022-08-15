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
import static org.hamcrest.Matchers.equalTo;


public class PostCourierTest {
    private final static Random random = new Random();
    private static String login;
    private static String password;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/api/v1";
        login = "test_courier_" + random.nextInt(1000);
        //System.out.println(login);
    }

    @Step("Send {methodName} request to {route}")
    public Response sendRequest(String route, String password, String firstName, String methodName) throws RuntimeException {
        PostCourierSerial courier  = new PostCourierSerial(login, password, firstName);
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

    @Step("Compare ok to true")
    public void compareOk(Response response, boolean ok){
        response.then().assertThat().body("ok", equalTo(ok));
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
    @DisplayName("POST /courier register courier with all fields successfully")
    public void validBodyTest() {
        Response response = sendRequest("/courier", "testpassword", "test_courier", "POST");
        compareOk(response, true);
        assertStatusCode(response, 201);

    }

    @Test
    @DisplayName("POST /courier don't register courier with the same login")
    public void twoEqualCourierTest() {
        Response response = sendRequest("/courier", "testpassword", "test_courier", "POST");
        assertStatusCode(response, 201);
        response = sendRequest("/courier", "test_courier", "test_courier", "POST");
        compareMessage(response, "Этот логин уже используется. Попробуйте другой.");
        assertStatusCode(response, 409);
    }

    @Test
    @DisplayName("POST /courier don't register courier without login")
    public void RegWithoutLoginTest () {
        login = "";
        Response response = sendRequest("/courier", "testpassword", "test_courier", "POST");
        compareCodeField(response, 400);
        compareMessage(response, "Недостаточно данных для создания учетной записи");
        assertStatusCode(response, 400);
        }

        @Test
        @DisplayName("POST /courier don't register courier without password")
        public void RegWithoutPasswordTest () {
            password = "";
            Response response = sendRequest("/courier", password, "test_courier", "POST");
            compareCodeField(response, 400);
            compareMessage(response, "Недостаточно данных для создания учетной записи");
            assertStatusCode(response, 400);
        }

        @Test
        @DisplayName("POST /courier register courier without firstName successfully")
        public void RegWithoutFirstNameTest () {
            Response response = sendRequest("/courier", "testpassword", "", "POST");
            compareOk(response,true);
            assertStatusCode(response, 201);
        }


        @After
        public void coolDown () {
            if (login == "" || password == "") {
                return;
            }

            LoginCourierSerial serialLogin = new LoginCourierSerial(login, "testpassword");
            int courierId = given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(serialLogin)
                    .when()
                    .post("/courier/login")
                    .then()
                    .extract()
                    .body()
                    .path("id");
            given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(serialLogin)
                    .when()
                    .delete("/courier/{courierId}", courierId)
                    .then().assertThat().statusCode(200);

        }
    }
