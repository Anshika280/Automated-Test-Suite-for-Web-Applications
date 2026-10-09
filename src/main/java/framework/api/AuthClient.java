package framework.api;

import framework.models.UserRequestModel;
import io.restassured.response.Response;

public class AuthClient extends BaseApiClient {

    private static final String LOGIN_ENDPOINT = "/api/login";
    private static final String REGISTER_ENDPOINT = "/api/register";

    public Response login(UserRequestModel authRequest) {
        return post(LOGIN_ENDPOINT, authRequest);
    }

    public Response register(UserRequestModel authRequest) {
        return post(REGISTER_ENDPOINT, authRequest);
    }
}
