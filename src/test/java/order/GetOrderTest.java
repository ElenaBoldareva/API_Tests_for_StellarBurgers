package order;

import APIHelper.OrderAPIHelper;
import APIHelper.UserAPIHelper;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class GetOrderTest {

    private OrderAPIHelper orderAPIHelper;
    private UserAPIHelper userAPIHelper;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        orderAPIHelper = new OrderAPIHelper();
        userAPIHelper = new UserAPIHelper();
    }

    @Test
    @DisplayName("Check status code of get orders")
    public void getOrdersTest() {
        String accessToken = userAPIHelper.createUser();
        Response response = orderAPIHelper.getOrders(accessToken);
        response.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
        userAPIHelper.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Check status code of get orders without authorization")
    public void getOrdersWithoutAuthorizationTest() {
        Response response = orderAPIHelper.getOrders("");
        response.then().assertThat().body("success", equalTo(false))
                .and()
                .statusCode(401);
    }
}
