package tests;

import steps.OrderSteps;
import urls.Urls;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.*;


public class GetOrdersTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = Urls.URL;
    }


    @Test
    @DisplayName("Check the body with orders")
    public void ordersArrayIsPresent() {
        OrderSteps steps = new OrderSteps();
        Response response = steps.sendRequestGet(Urls.ORDERS);
        steps.assertBody(response);
    }

}
