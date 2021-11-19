package nextstep.subway.utils;

import java.util.Map;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class RequestUtil {
    private RequestUtil() {
    }

    public static ExtractableResponse<Response> post(final String uri, final Map<String, Object> params) {
        return RestAssured
            .given().log().all()
            .body(params).contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post(uri)
            .then().log().all().extract();
    }

    public static ExtractableResponse<Response> get(final String uri) {
        return RestAssured
            .given().log().all()
            .when().get(uri)
            .then().log().all().extract();
    }

    public static ExtractableResponse<Response> put(final String uri, final Map<String, Object> params) {
        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put(uri)
            .then().log().all().extract();
    }

    public static ExtractableResponse<Response> delete(final String uri) {
        return RestAssured
            .given().log().all()
            .when().delete(uri)
            .then().log().all().extract();
    }
}
