package order;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.Order;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreateTest {
    private final static String BASE_URI = "http://qa-scooter.praktikum-services.ru/";
    OrderClient orderClient = new OrderClient();
    Order order = OrderGenerator.createRanndomOrder();
    private final String[] color;

    public OrderCreateTest(String[] color) {
        this.color = color;
    }

    @Parameterized.Parameters(name = "Предпочитаемые цвета")
    public static Object[][] getColor() {
        return new Object[][]{
                {new String[]{"BLACK"}},
                {new String[]{"BLACK", "GREY"}},
                {new String[]{"GREY"}},
                {new String[]{""}},
        };
    }


    @Before
    public void setup() {
        RestAssured.baseURI = BASE_URI;
    }

    @Test
    @DisplayName("Создание заказов с разными предпочитаемыми цветами")
    public void createOrder() {
        order.setColor(color);
        Response response = orderClient.create(order);
        int orderTrack = response.path("track");
        response.then()
                .statusCode(HttpStatus.SC_CREATED)
                .and()
                .assertThat().body("track", notNullValue());
        Response cancelResponse = orderClient.cancel(orderTrack);
        cancelResponse.then()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("ok", equalTo(true));

    }

}