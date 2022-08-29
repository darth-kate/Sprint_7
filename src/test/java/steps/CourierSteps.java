package steps;

import io.restassured.path.json.JsonPath;
import serial.PostCourierSerial;
import serial.LoginCourierSerial;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import urls.Urls;


import java.util.Locale;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;

public class CourierSteps {
    private static String login;
    private static String password;

    @Step("Compare ok to true")
    public void compareOk(Response response, boolean ok) {
        response.then().assertThat().body("ok", equalTo(ok));
    }

    @Step("Compare code")
    public void compareCodeField(Response response, int code) {
        response.then().assertThat().body("code", equalTo(code));
    }

    @Step("Compare message")
    public void compareMessage(Response response, String message) {
        response.then().assertThat().body("message", equalTo(message));
    }

    @Step("Assert status code of response")
    public void assertStatusCode(Response response, int statusCode) {
        response.then().assertThat().statusCode(statusCode);
    }

    @Step("Create courier")
    public Response createCourier(String route, String login, String password, String firstName) {
        PostCourierSerial courier = new PostCourierSerial(login, password, firstName);
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post(route);
        return response;
    }

    @Step("Send {methodName} request to {route}")
    public Response sendRequestLogin(String route, String login, String password, String methodName) throws RuntimeException {
        LoginCourierSerial courier = new LoginCourierSerial(login, password);
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

    @Step("Compare id")
    public void compareId(Response response) {
        response.then().assertThat().body("id", any(Integer.class));
    }

    @Step("Delete Courier")
    public Response deleteCourier(Integer courierId) {
        Response response = given()
                .header("Content-type", "application/json")
                .when()
                .delete(Urls.DELETE_COURIER, courierId);
        return response;
    }

    @Step("Extract courierId")
    public int extractCourierId(String route, String login, String password, String methodName) {
        Response response = sendRequestLogin(route, login, password, methodName);
        JsonPath jsonPathEvaluator = response.jsonPath();
        int courierId = jsonPathEvaluator.get("id");
        return  courierId;
    }
}
