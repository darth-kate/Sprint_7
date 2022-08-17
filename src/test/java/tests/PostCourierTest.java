package tests;

import serial.LoginCourierSerial;
import steps.CourierSteps;
import urls.Urls;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.*;
import java.util.Random;
import static org.apache.http.HttpStatus.*;

import static io.restassured.RestAssured.given;


public class PostCourierTest {

    private static String login;
    private static String password = "test_password";
    private final static Random random = new Random();

    @Before
    public void setUp() {
        RestAssured.baseURI = Urls.URL;
        login = "test_courier_" + random.nextInt(1000);
    }

    @Test
    @DisplayName("POST /courier register courier with all fields successfully")
    public void validBodyTest() {
        CourierSteps steps = new CourierSteps();
        Response response = steps.createCourier(Urls.POST_COURIER, login, password, "Courier");
        steps.compareOk(response, true);
        steps.assertStatusCode(response, SC_CREATED);

    }

    @Test
    @DisplayName("POST /courier don't register courier with the same login")
    public void twoEqualCourierTest() {
        CourierSteps steps = new CourierSteps();
        Response response = steps.createCourier(Urls.POST_COURIER, login, password, "Courier");
        steps.assertStatusCode(response, SC_CREATED);
        response = steps.createCourier(Urls.POST_COURIER, login, password, "Courier");
        steps.compareMessage(response, "Этот логин уже используется. Попробуйте другой.");
        steps.assertStatusCode(response, SC_CONFLICT);
    }

    @Test
    @DisplayName("POST /courier don't register courier without login")
    public void RegWithoutLoginTest() {
        login = "";
        CourierSteps steps = new CourierSteps();
        Response response = steps.createCourier(Urls.POST_COURIER, login, password, "Courier");
        steps.compareCodeField(response, SC_BAD_REQUEST);
        steps.compareMessage(response, "Недостаточно данных для создания учетной записи");
        steps.assertStatusCode(response, SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("POST /courier don't register courier without password")
    public void RegWithoutPasswordTest() {
        password = "";
        CourierSteps steps = new CourierSteps();
        Response response = steps.createCourier(Urls.POST_COURIER, login, password, "Courier");
        steps.compareCodeField(response, SC_BAD_REQUEST);
        steps.compareMessage(response, "Недостаточно данных для создания учетной записи");
        steps.assertStatusCode(response, SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("POST /courier register courier without firstName successfully")
    public void RegWithoutFirstNameTest() {
        CourierSteps steps = new CourierSteps();
        Response response = steps.createCourier(Urls.POST_COURIER, login, password, "Courier");
        steps.compareOk(response, true);
        steps.assertStatusCode(response, SC_CREATED);
    }


    @After
    public void coolDown() {
        if (login == "" || password == "") {
            return;
        }

        LoginCourierSerial serialLogin = new LoginCourierSerial(login, password);
        int courierId = given().header("Content-type", "application/json").and().body(serialLogin).when().post(Urls.LOGIN_COURIER).then().extract().body().path("id");
        given().header("Content-type", "application/json").and().body(serialLogin).when().delete("/courier/{courierId}", courierId).then().assertThat().statusCode(SC_OK);

    }
}
