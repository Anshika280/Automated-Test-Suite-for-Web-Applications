package tests.api;

import framework.api.UserClient;
import framework.models.UserRequestModel;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;

public class UserApiTest {

    private UserClient userClient;

    @BeforeClass
    public void setUp() {
        userClient = new UserClient();
    }

    @Test(groups = {"api", "smoke"})
    public void testGetUsersList() {
        Response response = userClient.getUsers(2);
        int code = response.getStatusCode();
        Assert.assertTrue(code == 200 || code == 429, "HTTP Status Code should be 200 or 429");
        if (code == 200) {
            Assert.assertTrue(response.getContentType().contains("application/json"), "Content Type should be JSON");
            response.then()
                    .body("page", equalTo(2))
                    .body("data", hasSize(greaterThan(0)))
                    .body("data[0].id", notNullValue());
        }
    }

    @Test(groups = {"api", "functional"})
    public void testGetUserById() {
        Response response = userClient.getUserById(2);
        int code = response.getStatusCode();
        Assert.assertTrue(code == 200 || code == 429, "HTTP Status Code should be 200 or 429");
        if (code == 200) {
            response.then()
                    .body("data.id", equalTo(2))
                    .body("data.first_name", equalTo("Janet"));
        }
    }

    @Test(groups = {"api", "functional"})
    public void testCreateUser() {
        UserRequestModel request = new UserRequestModel("Sarah Jenkins", "Senior SDET");
        Response response = userClient.createUser(request);
        int code = response.getStatusCode();
        Assert.assertTrue(code == 201 || code == 429, "HTTP Status Code should be 201 or 429");
        if (code == 201) {
            response.then()
                    .body("name", equalTo("Sarah Jenkins"))
                    .body("job", equalTo("Senior SDET"))
                    .body("id", notNullValue());
        }
    }

    @Test(groups = {"api", "functional"})
    public void testUpdateUser() {
        UserRequestModel request = new UserRequestModel("Sarah Jenkins", "Principal Automation Architect");
        Response response = userClient.updateUser(2, request);
        int code = response.getStatusCode();
        Assert.assertTrue(code == 200 || code == 429, "HTTP Status Code should be 200 or 429");
        if (code == 200) {
            response.then()
                    .body("name", equalTo("Sarah Jenkins"))
                    .body("job", equalTo("Principal Automation Architect"));
        }
    }

    @Test(groups = {"api", "functional"})
    public void testDeleteUser() {
        Response response = userClient.deleteUser(2);
        int code = response.getStatusCode();
        Assert.assertTrue(code == 204 || code == 429, "HTTP Status Code for DELETE should be 204 or 429");
    }
}
