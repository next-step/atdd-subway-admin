package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import java.util.Map;

public final class RestAssuredTemplate {
    private final String baseUrl;
    private final String baseUrlAndId;

    public RestAssuredTemplate(final String baseUrl) {
        this.baseUrl = baseUrl;
        this.baseUrlAndId = baseUrl + "/{id}";
    }

    public ExtractableResponse<Response> get() {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(baseUrl)
                .then().log().all()
                .extract();
    }

    public ExtractableResponse<Response> get(final Long id) {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", id)
                .when()
                .get(baseUrlAndId)
                .then().log().all()
                .extract();
    }

    public ExtractableResponse<Response> post(final Map<String, String> params) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when()
                .post(baseUrl)
                .then().log().all()
                .extract();
    }

    public ExtractableResponse<Response> put(final Long id, final Map<String, String> params) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", id)
                .body(params)
                .when()
                .put(baseUrlAndId)
                .then().log().all()
                .extract();
    }

    public ExtractableResponse<Response> delete(final Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", id)
                .when()
                .delete(baseUrlAndId)
                .then().log().all()
                .extract();
    }

    public ExtractableResponse<Response> delete(final String uri) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }

    public static long getLocationId(final ExtractableResponse<Response> response) {
        String result = response.header("Location").split("/")[2];

        return Long.parseLong(result);
    }
}
