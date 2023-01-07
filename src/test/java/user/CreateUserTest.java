package user;

import APIHelper.UserAPIHelper;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import pojo.User;
import util.UserUtils;

import static org.hamcrest.CoreMatchers.equalTo;

public class CreateUserTest {
    private UserAPIHelper userAPIHelper;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        userAPIHelper = new UserAPIHelper();
    }

    @Test
    @DisplayName("Check status code of create user")
    public void createNewUserTest() {
        User user = UserUtils.getRandomUser();
        Response response = userAPIHelper.createUser(user);
        response.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
        String accessToken = response.jsonPath().get("accessToken");
        userAPIHelper.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Check status code of create registered user")
    public void createRegisteredUserTest() {
        User user = UserUtils.getRandomUser();
        Response createResponse = userAPIHelper.createUser(user);
        Response createDoubleResponse = userAPIHelper.createUser(user);
        createDoubleResponse.then().assertThat().body("success", equalTo(false))
                .and()
                .statusCode(403);
        String accessToken = createResponse.jsonPath().get("accessToken");
        userAPIHelper.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Check status code of create user without email")
    public void createUserWithoutEmailTest() {
        User user = UserUtils.getRandomUser();
        user.setEmail(null);
        Response createResponse = userAPIHelper.createUser(user);
        createResponse.then().assertThat().body("success", equalTo(false))
                .and()
                .statusCode(403);
    }

    @Test
    @DisplayName("Check status code of create user without password")
    public void createUserWithoutPasswordTest() {
        User user = UserUtils.getRandomUser();
        user.setPassword(null);
        Response createResponse = userAPIHelper.createUser(user);
        createResponse.then().assertThat().body("success", equalTo(false))
                .and()
                .statusCode(403);
    }

    @Test
    @DisplayName("Check status code of create user without name")
    public void createUserWithoutNameTest() {
        User user = UserUtils.getRandomUser();
        user.setName(null);
        Response createResponse = userAPIHelper.createUser(user);
        createResponse.then().assertThat().body("success", equalTo(false))
                .and()
                .statusCode(403);
    }
}
