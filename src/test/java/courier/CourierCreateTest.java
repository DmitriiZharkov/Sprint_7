package courier;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.Courier;
import models.CourierCreds;
import models.CourierGenerator;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

public class CourierCreateTest {
    private static String BASE_URI = "http://qa-scooter.praktikum-services.ru/";
    private final CourierClient courierClient = new CourierClient();
    Courier courier = CourierGenerator.randomCourier();

    @Before
    public void setup() {
        RestAssured.baseURI = BASE_URI;
    }

    @Test
    @DisplayName("Создание нового курьера и вход под ним")
    public void createCourierTest() {
        Response response = courierClient.create(courier);
        assertEquals("Неверный статус код при создании курьера", HttpStatus.SC_CREATED, response.statusCode());
        response.then().assertThat().body("ok", equalTo(true));
        Response loginResponse = courierClient.login(CourierCreds.credsFrom(courier));
        assertEquals("Не удалось залогиниться", HttpStatus.SC_OK, loginResponse.statusCode());
    }
    @After
    public void tearDown() {
        Response response = courierClient.deleteCourier(courier);
        assertEquals("Ошибка при удалении курьера", HttpStatus.SC_OK, response.getStatusCode());
    }

}