package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class RestAssuredRequestBuilder {
    public RestAssuredRequestBuilder() {
    }

    public static ExtractableResponse<Response> postWithPathValue(Map params, String path, String pathValue, String mediaType) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(mediaType)
                .when().post(path, pathValue)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> post(Map params, String path, String mediaType) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(mediaType)
                .when().post(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> get(String path) {
        return RestAssured.given().log().all()
                .when().get(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> get(String path, Long id) {
        return RestAssured.given().log().all()
                .when().get(path, id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> delete(String path, Long id, HttpStatus status) {
        return RestAssured.given().log().all()
                .when().delete(path, id)
                .then().log().all()
                .statusCode(status.value())
                .extract();
    }

    public static ExtractableResponse<Response> delete(String path, String variable1, String variable2, HttpStatus status) {
        return RestAssured.given().log().all()
                .when().delete(path, variable1, variable2)
                .then().log().all()
                .statusCode(status.value())
                .extract();
    }

    public static ExtractableResponse<Response> put(Map params, String mediaType, String path, Long id, HttpStatus status) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(mediaType)
                .when().put(path, id)
                .then().log().all()
                .statusCode(status.value())
                .extract();
    }
}
