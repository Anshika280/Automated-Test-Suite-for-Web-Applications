package tests.api;

import framework.api.AuthClient;
import framework.models.UserRequestModel;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class AuthApiTest {

    private AuthClient authClient;

    @BeforeClass
    public void setUp() {
        authClient = new AuthClient();
    }

    @Test(groups = {"api", "smoke"})
    public void testSuccessfulLoginApi() {
        UserRequestModel request = new UserRequestModel("eve.holt@reqres.in", "cityslicka", true);
        Response response = authClient.login(request);
        int code = response.getStatusCode();
        if (code == 429) {
            throw new SkipException("External API ReqRes returned HTTP 429 (Rate Limit). Skipping test.");
        }
        Assert.assertEquals(code, 200, "HTTP Status Code should be 200 OK");
        response.then().body("token", notNullValue());
    }

    @Test(groups = {"api", "functional"})
    public void testUnsuccessfulLoginMissingPasswordApi() {
        UserRequestModel request = new UserRequestModel("peter@klaven", "", true);
        Response response = authClient.login(request);
        int code = response.getStatusCode();
        if (code == 429) {
            throw new SkipException("External API ReqRes returned HTTP 429 (Rate Limit). Skipping test.");
        }
        Assert.assertEquals(code, 400, "HTTP Status Code should be 400 Bad Request");
        response.then().body("error", equalTo("Missing password"));
    }
}
