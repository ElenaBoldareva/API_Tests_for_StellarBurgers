package order;

import APIHelper.OrderAPIHelper;
import APIHelper.UserAPIHelper;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import pojo.Ingredient;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;

public class CreateOrderTest {

    private OrderAPIHelper orderAPIHelper;
    private UserAPIHelper userAPIHelper;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        orderAPIHelper = new OrderAPIHelper();
        userAPIHelper = new UserAPIHelper();
    }

    @Test
    @DisplayName("Check status code of create order")
    public void createOrderTest() {
        String accessToken = userAPIHelper.createUser();
        List<Ingredient> ingredients = orderAPIHelper.getIngredients();
        List<String> ingredientIds = new ArrayList<>();
        ingredientIds.add(ingredients.get(0).get_id());
        ingredientIds.add(ingredients.get(1).get_id());
        ingredientIds.add(ingredients.get(2).get_id());
        Response response = orderAPIHelper.createOrder(ingredientIds, accessToken);
        response.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
        userAPIHelper.deleteUser(accessToken);
    }

    /**
     * The test fails because it's a bug.
     * According to the API description an unauthorized user can't create an order.
     */
    @Test
    @DisplayName("Check status code of create order by unauthorized user")
    public void createOrderByUnauthorizedUserTest() {
        List<Ingredient> ingredients = orderAPIHelper.getIngredients();
        List<String> ingredientIds = new ArrayList<>();
        ingredientIds.add(ingredients.get(0).get_id());
        ingredientIds.add(ingredients.get(1).get_id());
        ingredientIds.add(ingredients.get(2).get_id());
        Response response = orderAPIHelper.createOrder(ingredientIds, "");
        response.then().assertThat().body("success", equalTo(false))
                .and()
                .statusCode(401);
    }

    @Test
    @DisplayName("Check status code of create order without ingredients")
    public void createOrderWithoutIngredientsTest() {
        String accessToken = userAPIHelper.createUser();
        List<String> ingredientIds = new ArrayList<>();
        Response response = orderAPIHelper.createOrder(ingredientIds, accessToken);
        response.then().assertThat().body("success", equalTo(false))
                .and()
                .statusCode(400);
        userAPIHelper.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Check status code of create order")
    public void createOrderWithWrongIngredientsTest() {
        String accessToken = userAPIHelper.createUser();
        List<String> ingredientIds = new ArrayList<>();
        ingredientIds.add("wrongid");
        Response response = orderAPIHelper.createOrder(ingredientIds, accessToken);
        response.then().assertThat()
                .statusCode(500);
        userAPIHelper.deleteUser(accessToken);
    }
}
