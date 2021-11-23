package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.springframework.http.MediaType;

public final class RestAssuredUtils {

    private RestAssuredUtils() {
    }

    public static ValidatableResponse get(final String path, final Object... pathParams) {
        return RestAssured.given().log().all()
            .when()
            .get(path, pathParams)
            .then().log().all();
    }

    public static ValidatableResponse post(final Object body, final String path, final Object... pathParams) {
        return RestAssured.given().log().all()
            .body(body)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(path, pathParams)
            .then().log().all();
    }

    public static ValidatableResponse put(final Object body, final String path, final Object... pathParams) {
        return RestAssured.given().log().all()
            .body(body)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(path, pathParams)
            .then().log().all();
    }

    public static ValidatableResponse patch(final Object body, final String path, final Object... pathParams) {
        return RestAssured.given().log().all()
            .body(body)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .patch(path, pathParams)
            .then().log().all();
    }

    public static ValidatableResponse delete(final String path, final Object... pathParams) {
        return RestAssured.given().log().all()
            .when()
            .delete(path, pathParams)
            .then().log().all();
    }

    public static ValidatableResponse options(final String path, final Object... pathParams) {
        return RestAssured.given().log().all()
            .when()
            .options(path, pathParams)
            .then().log().all();
    }
}
