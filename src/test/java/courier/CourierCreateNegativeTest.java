package courier;

import courier.CourierClient;
import courier.CourierGenerator;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.Courier;
import org.apache.http.HttpStatus;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static utils.Utils.randomString;

public class CourierCreateNegativeTest {
    private static String BASE_URI = "http://qa-scooter.praktikum-services.ru/";
    private final CourierClient courierClient = new CourierClient();
    Courier courier =  CourierGenerator.randomCourier();
    @Before
    public void setup(){
        RestAssured.baseURI = BASE_URI;
    }
    @Test
    @DisplayName("Проверка, что нельзя создать одинаковых курьеров")
    public void createSameCourierReturnConflictTest(){
        Courier sameCourier = new Courier()
                .withLogin(courier.getLogin())
                .withPassword(randomString(8))
                .withFirstName(randomString(10));
        Response firstResponse = courierClient.create(courier);
        assertEquals("Неверный статус код при создании курьера", HttpStatus.SC_CREATED, firstResponse.statusCode());
        Response sameResponse = courierClient.create(sameCourier);
        sameResponse
                .then()
                .statusCode(HttpStatus.SC_CONFLICT)
                .and()
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }
    @Test
    @DisplayName("Создание курьера без логина")
    public void noLoginReturnBadRequest(){
        Courier courierNoLogin = new Courier()
                .withPassword(randomString(20))
                .withFirstName(randomString(10));
        Response noLoginResponse = courierClient.create(courierNoLogin);
        noLoginResponse
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
    @Test
    @DisplayName("Создание курьера без пароля")
    public void noPassReturnBadRequest(){
        Courier courierNoPass = new Courier()
                .withLogin(randomString(20))
                .withFirstName(randomString(10));
        Response noPassResponse = courierClient.create(courierNoPass);
        noPassResponse
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}