package framework.api;

import framework.config.ConfigManager;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;

public abstract class BaseApiClient {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected RequestSpecification getRequestSpec() {
        String baseUrl = ConfigManager.getApiUrl();
        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
    }

    protected Response get(String endpoint) {
        log.info("Sending GET request to endpoint [{}]", endpoint);
        return given()
                .spec(getRequestSpec())
                .when()
                .get(endpoint);
    }

    protected Response post(String endpoint, Object body) {
        log.info("Sending POST request to endpoint [{}]", endpoint);
        return given()
                .spec(getRequestSpec())
                .body(body)
                .when()
                .post(endpoint);
    }

    protected Response put(String endpoint, Object body) {
        log.info("Sending PUT request to endpoint [{}]", endpoint);
        return given()
                .spec(getRequestSpec())
                .body(body)
                .when()
                .put(endpoint);
    }

    protected Response delete(String endpoint) {
        log.info("Sending DELETE request to endpoint [{}]", endpoint);
        return given()
                .spec(getRequestSpec())
                .when()
                .delete(endpoint);
    }
}
