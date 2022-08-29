package tests;
import io.restassured.path.json.JsonPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import steps.OrderSteps;
import urls.Urls;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.*;
import static org.apache.http.HttpStatus.*;

import java.util.Random;

public class GetOneOrderTest {
    private final static Random random = new Random();
    private static int track;

    @Before
    public void setUp() {
        RestAssured.baseURI = Urls.URL;
        OrderSteps steps = new OrderSteps();
        track = steps.extractTrack();
    }

    @Test
    @DisplayName("Get one order successfully and assert some field")
    public void getOrderTest() {
        OrderSteps steps = new OrderSteps();
        Response response = steps.getOrder(track);
        steps.assertStatusCode(response, SC_OK);
        JsonPath jsonPathEvaluator = response.jsonPath();
        assertThat(jsonPathEvaluator.get("order.id"), notNullValue());
        assertThat(jsonPathEvaluator.get("order.firstName"), equalTo("test"));
        assertThat(jsonPathEvaluator.get("order.lastName"), equalTo("client"));
        assertThat(jsonPathEvaluator.get("order.address"), equalTo("test address 10"));
        assertThat(jsonPathEvaluator.get("order.metroStation"), equalTo("4"));
        assertThat(jsonPathEvaluator.get("order.rentTime"), equalTo(5));
        assertThat(jsonPathEvaluator.get("order.comment"), equalTo("1"));
    }

    @Test
    @DisplayName("Get an order with invalid track")
    public void sendInvalidOrderIdTest() {
        OrderSteps steps = new OrderSteps();
        Response response = steps.getOrder(random.nextInt(1000000));
        steps.assertStatusCode(response, SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Get one order successfully and assert some field")
    public void getOrderWithoutOrderIdTest() {
        OrderSteps steps = new OrderSteps();
        Response response = steps.getOrder(null);
        steps.assertStatusCode(response, SC_BAD_REQUEST);
    }


}
