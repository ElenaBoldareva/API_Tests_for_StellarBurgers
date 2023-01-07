package APIHelper;

import io.restassured.response.Response;
import pojo.Order;

import static io.restassured.RestAssured.given;

public class OrderAPIHelper {
    private static final String ORDER_URL = "/api/orders/";


    public Response createOrder(Order order) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(order)
                        .when()
                        .post(ORDER_URL);
        return response;
    }
}
