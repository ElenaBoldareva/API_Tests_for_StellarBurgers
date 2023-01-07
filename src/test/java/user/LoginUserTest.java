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

public class LoginUserTest {
    private UserAPIHelper userAPIHelper;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        userAPIHelper = new UserAPIHelper();
    }

    @Test
    @DisplayName("Check status code of login user")
    public void loginUserTest() {
        User user = UserUtils.getRandomUser();
        userAPIHelper.createUser(user);
        Response loginResponse = userAPIHelper.loginUser(user);
        loginResponse.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
        String accessToken = loginResponse.jsonPath().get("accessToken");
        userAPIHelper.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Check status code of login user with wrong email")
    public void loginUserWithWrongEmailTest() {
        User user = UserUtils.getRandomUser();
        Response loginResponse = userAPIHelper.loginUser(user);
        loginResponse.then().assertThat().body("success", equalTo(false))
                .and()
                .statusCode(401);
    }

    @Test
    @DisplayName("Check status code of login user with wrong password")
    public void loginUserWithWrongPasswordTest() {
        User user = UserUtils.getRandomUser();
        Response createResponse = userAPIHelper.createUser(user);
        user.setPassword(user.getPassword() + "1230");
        Response loginResponse = userAPIHelper.loginUser(user);
        loginResponse.then().assertThat().body("success", equalTo(false))
                .and()
                .statusCode(401);
        String accessToken = createResponse.jsonPath().get("accessToken");
        userAPIHelper.deleteUser(accessToken);
    }
}
