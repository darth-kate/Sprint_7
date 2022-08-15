import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.any;

@RunWith(Parameterized.class)
public class PostOrdersTest {
    private final String[] colors;

    public PostOrdersTest(String[] colors) {
        this.colors = colors;
    }

    @Parameterized.Parameters
    public static Object[][] getColors() {
        return new Object[][]{
                {new String[]{"GRAY", "BLACK"}},
                {new String[]{"GRAY"}},
                {new String[]{"BLACK"}},
                {new String[]{}}
        };
    }

    @Before
    public void setUp() {
    RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/api/v1";
    }

    @Step("Send request")
    public Response sendRequest(String firstName, String lastName, String address, int metroStation, String phone, int rentTime, String deliveryDate, String comment, String[] color){
        PostOrdersTestSerial courier  = new PostOrdersTestSerial(firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, color);
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/orders");
        return response;
    }

    @Step("Assert status code of response")
    public void assertStatusCode(Response response, int statusCode){
        response.then().assertThat().statusCode(statusCode);
    }

    @Step("Compare track")
    public void assertTrack(Response response){
        response.then().assertThat().body("track", any(Integer.class));
    }

    @Test
    @DisplayName("Order can be created with different colors and without colors at all")
    public void createOrderSuccessfully(){
        Response response = sendRequest("test", "client", "test address 10", 4, "+7 800 355 35 35", 5, "2022-09-01", "1", colors);
        assertStatusCode(response, 201);
        assertTrack(response);
    }

}
