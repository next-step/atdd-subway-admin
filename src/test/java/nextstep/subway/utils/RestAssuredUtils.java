package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.MediaType;

public class RestAssuredUtils<T> {

    private static RequestSpecification requestSpecification;

    static {
        requestSpecification = RestAssured.given()
            .log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE);
    }

    public static <T> ValidatableResponse post(final String urlTemplate, T request) {
        return requestSpecification.when()
            .body(request)
            .post(urlTemplate)
            .then().log().all();
    }

    public static ValidatableResponse get(final String urlTemplate) {
        return requestSpecification.when()
            .get(urlTemplate)
            .then().log().all();
    }

    public static ValidatableResponse delete(final String urlTemplate, final String path) {
        return requestSpecification.when()
            .delete(makeUrlTemplate(urlTemplate, path))
            .then().log().all();
    }

    private static String makeUrlTemplate(String urlTemplate, String path) {
        return String.format(urlTemplate.concat("/%s"), path);
    }
}
