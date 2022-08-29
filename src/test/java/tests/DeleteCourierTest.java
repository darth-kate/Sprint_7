package tests;
import serial.LoginCourierSerial;
import serial.PostCourierSerial;
import steps.CourierSteps;
import urls.Urls;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.*;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;

import java.util.Random;

public class DeleteCourierTest {
    private final static Random random = new Random();
    private static String login;
    private static String password;
    private static String firstName;

    @Before
    public void setUp() {
        RestAssured.baseURI = Urls.URL;
        login = "test_courier_" + random.nextInt(1000);
        password = "password";
        firstName = "test_courier";
        CourierSteps steps = new CourierSteps();
        steps.createCourier(Urls.POST_COURIER, login, password, firstName);

    }

    @Test
    @DisplayName("DELETE courier with courierId successfully")
    public void validIdTest() {
        CourierSteps steps = new CourierSteps();
        int courierId = steps.extractCourierId(Urls.LOGIN_COURIER, login, password, "POST");
        Response response = steps.deleteCourier(courierId);
        steps.compareOk(response, true);
        steps.assertStatusCode(response, SC_OK);
    }

    @Test
    @DisplayName("DELETE courier with incorrect courierId returns 400")
    public void invalidIdTest() {
        CourierSteps steps = new CourierSteps();
        Response response = steps.deleteCourier(random.nextInt(1000));
        steps.assertStatusCode(response, SC_NOT_FOUND);
    }

    @Test
    @DisplayName("DELETE courier without courierId returns 400")
    public void withoutIdTest() {
        CourierSteps steps = new CourierSteps();
        Response response = given()
                .header("Content-type", "application/json")
                .when()
                .delete(Urls.POST_COURIER);
        steps.assertStatusCode(response, SC_NOT_FOUND);
    }
}
