package Tests;

import Serial.LoginCourierSerial;
import Steps.CourierSteps;
import URLs.Urls;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.*;
import static org.apache.http.HttpStatus.*;

import java.util.Random;

import static io.restassured.RestAssured.given;


public class PostCourierLoginTest {
    private final static Random random = new Random();
    private static String login;
    private static String password;

    @Before
    public void setUp() {
        RestAssured.baseURI = Urls.URL;
        login = "test_courier_" + random.nextInt(1000);
        password = "testpassword";
    }

    @Test
    @DisplayName("POST /courier/login log in courier with all fields successfully")
    public void validBodyTest() {
        CourierSteps steps = new CourierSteps();
        steps.createCourier(Urls.POST_COURIER, login, password, "test_courier");
        Response response = steps.sendRequestLogin(Urls.LOGIN_COURIER, login, password, "POST");
        steps.compareId(response);
        steps.assertStatusCode(response, SC_OK);
    }

    @Test
    @DisplayName("POST /courier/login don't log in courier without login field")
    public void loginWithoutLoginTest() {
        CourierSteps steps = new CourierSteps();
        steps.createCourier("/courier", login, password, "test_courier");
        Response response = steps.sendRequestLogin(Urls.LOGIN_COURIER, "", password, "POST");
        steps.compareCodeField(response, SC_BAD_REQUEST);
        steps.compareMessage(response, "Недостаточно данных для входа");
        steps.assertStatusCode(response, SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("POST /courier/login don't log in courier without password field")
    public void loginWithoutPasswordTest() {
        CourierSteps steps = new CourierSteps();
        steps.createCourier(Urls.POST_COURIER, login, password, "test_courier");
        Response response = steps.sendRequestLogin(Urls.LOGIN_COURIER, login, "", "POST");
        steps.compareCodeField(response, SC_BAD_REQUEST);
        steps.compareMessage(response, "Недостаточно данных для входа");
        steps.assertStatusCode(response, SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("POST /courier/login don't log in courier with non-existent values")
    public void loginWithFakeCourierTest() {
        login = String.valueOf(random.nextInt(100));
        password = String.valueOf(random.nextInt(100));
        CourierSteps steps = new CourierSteps();

        Response response = steps.sendRequestLogin(Urls.LOGIN_COURIER, login, password, "POST");
        steps.compareCodeField(response, SC_NOT_FOUND);
        steps.compareMessage(response, "Учетная запись не найдена");
        steps.assertStatusCode(response, SC_NOT_FOUND);

    }

    @After
    public void coolDown() {
        if (!login.contains("test_courier_")) {
            return;
        }
        LoginCourierSerial courier = new LoginCourierSerial(login, password);
        int courierId = given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post(Urls.LOGIN_COURIER)
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
                .then().assertThat().statusCode(SC_OK);

    }


}
