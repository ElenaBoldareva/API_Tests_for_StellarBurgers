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
        String accessToken = createResponse.path("accessToken");
        Response changeResponse = userAPIHelper.changeUser(changedUser, accessToken);

        changeResponse.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
        String email = changeResponse.path("user.email");
        String name = changeResponse.path("user.name");

        Assert.assertThat(email, equalToIgnoringCase(changedUser.getEmail()));
        Assert.assertThat(name, equalToIgnoringCase(changedUser.getName()));

        Response loginResponse = userAPIHelper.loginUser(changedUser);
        loginResponse.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);

        accessToken = loginResponse.path("accessToken");
        userAPIHelper.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Check status code of change authorized user with existing email")
    public void changeAuthorizedUserWithExistingEmailTest() {
        User firstUser = UserUtils.getRandomUser();
        Response createFirstUserResponse = userAPIHelper.createUser(firstUser);
        String firstUserAccessToken = createFirstUserResponse.path("accessToken");
        User secondUser = UserUtils.getRandomUser();
        Response createSecondUserResponse = userAPIHelper.createUser(secondUser);
        String secondUserAccessToken = createSecondUserResponse.path("accessToken");

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
        String accessToken = createResponse.path("accessToken");
        User changedUser = UserUtils.getRandomUser();
        Response changeResponse = userAPIHelper.changeUser(changedUser, "");
        changeResponse.then().assertThat().body("success", equalTo(false))
                .and()
                .statusCode(401);
        userAPIHelper.deleteUser(accessToken);
    }

}
