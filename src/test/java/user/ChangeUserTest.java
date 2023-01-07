package user;

import APIHelper.UserAPIHelper;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pojo.User;
import util.UserUtils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

public class ChangeUserTest {

    private UserAPIHelper userAPIHelper;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        userAPIHelper = new UserAPIHelper();
    }

    @Test
    @DisplayName("Check status code of change authorized user")
    public void changeAuthorizedUserTest() {
        User user = UserUtils.getRandomUser();
        Response createResponse = userAPIHelper.createUser(user);
        User changedUser = UserUtils.getRandomUser();
        String accessToken = createResponse.jsonPath().get("accessToken");
        Response changeResponse = userAPIHelper.changeUser(changedUser, accessToken);

        changeResponse.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
        String email = changeResponse.jsonPath().get("user.email");
        String name = changeResponse.jsonPath().get("user.name");

//        Assert.assertEquals(changedUser.getEmail(), email);
        Assert.assertThat(email, equalToIgnoringCase(changedUser.getEmail()));
//        Assert.assertEquals(changedUser.getName(), name);
        Assert.assertThat(name, equalToIgnoringCase(changedUser.getName()));

        Response loginResponse = userAPIHelper.loginUser(changedUser);
        loginResponse.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);

        accessToken = loginResponse.jsonPath().get("accessToken");
        userAPIHelper.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Check status code of change authorized user with existing email")
    public void changeAuthorizedUserWithExistingEmailTest() {
        User firstUser = UserUtils.getRandomUser();
        Response createFirstUserResponse = userAPIHelper.createUser(firstUser);
        String firstUserAccessToken = createFirstUserResponse.jsonPath().get("accessToken");
        User secondUser = UserUtils.getRandomUser();
        Response createSecondUserResponse = userAPIHelper.createUser(secondUser);
        String secondUserAccessToken = createSecondUserResponse.jsonPath().get("accessToken");

        firstUser.setEmail(secondUser.getEmail());
        Response changeResponse = userAPIHelper.changeUser(firstUser, firstUserAccessToken);
        changeResponse.then().assertThat().body("success", equalTo(false))
                .and()
                .statusCode(403);

        userAPIHelper.deleteUser(firstUserAccessToken);
        userAPIHelper.deleteUser(secondUserAccessToken);
    }

    @Test
    @DisplayName("Check status code of change unauthorized user")
    public void changeUnauthorizedUserTest() {
        User user = UserUtils.getRandomUser();
        Response createResponse = userAPIHelper.createUser(user);
        String accessToken = createResponse.jsonPath().get("accessToken");
        User changedUser = UserUtils.getRandomUser();
        Response changeResponse = userAPIHelper.changeUser(changedUser, "");
        changeResponse.then().assertThat().body("success", equalTo(false))
                .and()
                .statusCode(401);
        userAPIHelper.deleteUser(accessToken);
    }

}
