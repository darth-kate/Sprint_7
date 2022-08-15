import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.*;
import io.qameta.allure.Step;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.notNullValue;

public class GetOrdersTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/api/v1";
    }

    @Step("Send request")
    public Response sendRequest(String route){
        return given()
                .header("Content-type", "application/json")
                .get(route);
    }

    @Step("Parse the body and assert the orders[]")
    public void assertBody(Response response){
        GetOrderSerial order = response.getBody().as(GetOrderSerial.class);
        MatcherAssert.assertThat(order.getOrders(), notNullValue());
    }


    @Test
    @DisplayName("Check the body with orders[git@github.com:darth-kate/Sprint_7.git]")
    public void ordersArrayIsPresent() {
        Response response = sendRequest("/orders");
        assertBody(response);
    }

}
