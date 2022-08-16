package Steps;

import Serial.GetOrderSerial;
import Serial.PostOrdersTestSerial;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import URLs.Urls;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.notNullValue;

public class OrderSteps {
    @Step("Send request")
    public Response sendRequestPost(String firstName, String lastName, String address, int metroStation, String phone, int rentTime, String deliveryDate, String comment, String[] color) {
        PostOrdersTestSerial courier = new PostOrdersTestSerial(firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, color);
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post(Urls.ORDERS);
        return response;
    }

    @Step("Send request")
    public Response sendRequestGet(String route) {
        return given()
                .header("Content-type", "application/json")
                .get(route);
    }

    @Step("Assert status code of response")
    public void assertStatusCode(Response response, int statusCode) {
        response.then().assertThat().statusCode(statusCode);
    }

    @Step("Compare track")
    public void assertTrack(Response response) {
        response.then().assertThat().body("track", any(Integer.class));
    }

    @Step("Parse the body and assert the orders[]")
    public void assertBody(Response response) {
        GetOrderSerial order = response.getBody().as(GetOrderSerial.class);
        MatcherAssert.assertThat(order.getOrders(), notNullValue());
    }

}

