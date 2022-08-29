package steps;

import io.restassured.path.json.JsonPath;
import serial.GetOrderSerial;
import serial.PostOrdersTestSerial;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import urls.Urls;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.notNullValue;

public class OrderSteps {
    @Step("Send POST request")
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

    @Step("Send GET request")
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

    @Step("Extract track")
    public int extractTrack() {
        OrderSteps steps = new OrderSteps();
        Response response = steps.sendRequestPost("test", "client", "test address 10", 4, "+7 800 355 35 35", 5, "2022-09-01", "1", new String[]{"GRAY", "BLACK"});
        assertStatusCode(response, SC_CREATED);
        int track = response
                .then()
                .extract()
                .body()
                .path("track");
        return  track;
    }

    @Step("Extract orderId with track")
    public int extractOrderId(int track) {
        Response response = given()
                .header("Content-type", "application/json")
                .queryParam("t",track).get(Urls.GET_ORDER);
        assertStatusCode(response, SC_OK);
        JsonPath jsonPathEvaluator = response.jsonPath();
        int orderId = jsonPathEvaluator.get("order.id");
        return orderId;
    }

    @Step("Send PUT request - accept the order with courier")
    public Response sendRequestPut(Integer orderId, Integer courierId) {
        return given()
                .header("Content-type", "application/json")
                .queryParam("courierId", courierId).put(Urls.PUT_ORDER, orderId);
    }

}

