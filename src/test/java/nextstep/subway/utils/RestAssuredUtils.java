package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import java.util.Map;
import org.springframework.http.MediaType;

public class RestAssuredUtils {

    private static RequestSpecification requestSpecification;

    static {
        requestSpecification = RestAssured.given()
            .log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE);
    }

    public static ValidatableResponse post(final String urlTemplate, final Map<String, String> requestParam) {
        return requestSpecification.when()
            .body(requestParam)
            .post(urlTemplate)
            .then().log().all();
    }

    public static ValidatableResponse get(final String urlTemplate) {
        return requestSpecification.when()
            .get(urlTemplate)
            .then().log().all();
    }

    public static ValidatableResponse delete(final String urlTemplate) {
        return requestSpecification.when()
            .delete(urlTemplate)
            .then().log().all();
    }
}
