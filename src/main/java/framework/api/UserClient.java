package framework.api;

import framework.models.UserRequestModel;
import io.restassured.response.Response;

public class UserClient extends BaseApiClient {

    private static final String USERS_ENDPOINT = "/api/users";

    public Response getUsers(int page) {
        return get(USERS_ENDPOINT + "?page=" + page);
    }

    public Response getUserById(int id) {
        return get(USERS_ENDPOINT + "/" + id);
    }

    public Response createUser(UserRequestModel userRequest) {
        return post(USERS_ENDPOINT, userRequest);
    }

    public Response updateUser(int id, UserRequestModel userRequest) {
        return put(USERS_ENDPOINT + "/" + id, userRequest);
    }

    public Response deleteUser(int id) {
        return delete(USERS_ENDPOINT + "/" + id);
    }
}
