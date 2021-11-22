package nextstep.subway;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.MediaType;

public class RestAssuredDelegator {
    private final RequestSpecification requestSpecification;
    private Response response;

    private RestAssuredDelegator(final RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification;
    }

    public static RestAssuredDelegator given() {
        return new RestAssuredDelegator(RestAssured.given().log().all());
    }

    public static RestAssuredDelegator given(final Object body) {
        return new RestAssuredDelegator(RestAssured.given().log().all()
            .body(body)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
    }

    public RestAssuredDelegator get(final String path, final Object... pathParams) {
        response = this.requestSpecification.when().get(path, pathParams);
        return this;
    }

    public RestAssuredDelegator post(final String path, final Object... pathParams) {
        response = requestSpecification.when().post(path, pathParams);
        return this;
    }

    public RestAssuredDelegator put(final String path, final Object... pathParams) {
        response = requestSpecification.when().put(path, pathParams);
        return this;
    }

    public RestAssuredDelegator patch(final String path, final Object... pathParams) {
        response = requestSpecification.when().patch(path, pathParams);
        return this;
    }

    public RestAssuredDelegator delete(final String path, final Object... pathParams) {
        response = requestSpecification.when().delete(path, pathParams);
        return this;
    }

    public RestAssuredDelegator options(final String path, final Object... pathParams) {
        response = requestSpecification.when().options(path, pathParams);
        return this;
    }

    public ValidatableResponse then() {
        return response.then().log().all();
    }
}
