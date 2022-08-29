package tests;

import org.junit.After;
import steps.CourierSteps;
import steps.OrderSteps;
import urls.Urls;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.*;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;

public class PutOrderTest {
    private final static Random random = new Random();
    private static String login;
    private static String password;
    private static String firstName;
    private static int orderId;
    private static int courierId;


    @Before
    public void setUp() {
        RestAssured.baseURI = Urls.URL;
        login = "test_courier_" + random.nextInt(1000);
        password = "password";
        firstName = "test_courier";
        CourierSteps steps = new CourierSteps();
        OrderSteps osteps = new OrderSteps();
        steps.createCourier(Urls.POST_COURIER, login, password, firstName);
        courierId = steps.extractCourierId(Urls.LOGIN_COURIER, login, password, "POST");
        int track = osteps.extractTrack();
        orderId = osteps.extractOrderId(track);
    }

    @Test
    @DisplayName("Accept the order successfully")
    public void validOrderValidCourierTest() {
        OrderSteps osteps = new OrderSteps();
        CourierSteps steps = new CourierSteps();
        Response response = osteps.sendRequestPut(orderId, courierId);
        osteps.assertStatusCode(response, SC_OK);
        steps.compareOk(response, true);
    }

    @Test
    @DisplayName("Accepting the order without courierId returns 400")
    public void acceptWithoutCourierIdTest() {
        OrderSteps osteps = new OrderSteps();
        Response response = osteps.sendRequestPut(orderId, null);
        osteps.assertStatusCode(response, SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Accepting the order without orderId returns 400")
    public void acceptWithoutOrderIdTest() {
        OrderSteps osteps = new OrderSteps();
        Response response = given()
                .header("Content-type", "application/json")
                .queryParam("courierId", courierId).put(Urls.INVALID_PUT_ORDER);
        osteps.assertStatusCode(response, SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Accepting the order without courierId returns 400")
    public void acceptWithInvalidCourierIdTest() {
        OrderSteps osteps = new OrderSteps();
        Response response = osteps.sendRequestPut(orderId, random.nextInt(1000000));
        osteps.assertStatusCode(response, SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Accepting the order without courierId returns 400")
    public void acceptWithInvalidOrderIdTest() {
        OrderSteps osteps = new OrderSteps();
        Response response = osteps.sendRequestPut(random.nextInt(1000000), courierId);
        osteps.assertStatusCode(response, SC_NOT_FOUND);
    }

    @After
    public void coolDown() {
        CourierSteps steps = new CourierSteps();
        Response response = steps.deleteCourier(courierId);
        steps.assertStatusCode(response, SC_OK);
    }
}
