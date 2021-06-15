package nextstep.subway.utils;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class CommonRequest {
    public static ExtractableResponse<Response> get(String path) {
        return RestAssured.given().log().all()
            .when()
            .get(path)
            .then().log().all()
            .extract();
    }

    public static <T> ExtractableResponse<Response> post(T request, String path) {
        return RestAssured.given().log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(path)
            .then().log().all()
            .extract();
    }

    public static <T> ExtractableResponse<Response> put(T request, String path) {
        return RestAssured.given().log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(path)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> delete(String path) {
        return RestAssured.given().log().all()
            .when()
            .delete(path)
            .then().log().all()
            .extract();
    }
}
