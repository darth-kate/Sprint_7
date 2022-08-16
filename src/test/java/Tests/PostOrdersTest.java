package Tests;

import Steps.OrderSteps;
import URLs.Urls;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.apache.http.HttpStatus.*;

@RunWith(Parameterized.class)
public class PostOrdersTest {
    private final String[] colors;

    public PostOrdersTest(String[] colors) {
        this.colors = colors;
    }

    @Parameterized.Parameters(name = "Тестовые данные: {index}")
    public static Object[][] getColors() {
        return new Object[][]{{new String[]{"GRAY", "BLACK"}}, {new String[]{"GRAY"}}, {new String[]{"BLACK"}}, {new String[]{}}};
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = Urls.URL;
    }


    @Test
    @DisplayName("Order can be created with different colors and without colors at all")
    public void createOrderSuccessfully() {
        OrderSteps steps = new OrderSteps();
        Response response = steps.sendRequestPost("test", "client", "test address 10", 4, "+7 800 355 35 35", 5, "2022-09-01", "1", colors);
        steps.assertStatusCode(response, SC_CREATED);
        steps.assertTrack(response);
    }

}
