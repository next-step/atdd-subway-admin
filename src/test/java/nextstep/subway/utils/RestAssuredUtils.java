package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import nextstep.subway.dto.UpdateLineRequest;
import org.springframework.http.MediaType;

public class RestAssuredUtils {

    private static RequestSpecification requestSpecification;

    static {
        requestSpecification = RestAssured.given()
            .log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE);
    }

    public static <T> ValidatableResponse post(final String urlTemplate, final T request) {
        return requestSpecification.when()
            .body(request)
            .post(urlTemplate)
            .then().log().all();
    }

    public static <T> ValidatableResponse post(final String urlTemplate, final Long path, final T request) {
        return requestSpecification.when()
            .body(request)
            .post(makeEndPoint(urlTemplate, path))
            .then().log().all();
    }

    public static ValidatableResponse get(final String urlTemplate) {
        return requestSpecification.when()
            .get(urlTemplate)
            .then().log().all();
    }

    public static ValidatableResponse get(final String urlTemplate, final Long path) {
        return requestSpecification.when()
            .get(makeEndPoint(urlTemplate, path))
            .then().log().all();
    }

    public static <T> ValidatableResponse delete(final String urlTemplate, final Long path, final T... queryParams) {
        return requestSpecification.when()
            .delete(urlTemplate, path, queryParams)
            .then().log().all();
    }

    public static ValidatableResponse delete(final String urlTemplate, final Long path) {
        return requestSpecification.when()
            .delete(makeEndPoint(urlTemplate, path))
            .then().log().all();
    }

    public static ValidatableResponse put(final String urlTemplate, final Long path,
        final UpdateLineRequest updateLineRequest) {
        return requestSpecification.when()
            .body(updateLineRequest)
            .put(makeEndPoint(urlTemplate, path))
            .then().log().all();
    }

    private static String makeEndPoint(final String urlTemplate, final Long path) {
        return String.format(urlTemplate.concat("/%s"), path);
    }
}
