package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.Map;

public class RestAssuredApiTest {

    public static ExtractableResponse<Response> get(String url, Long id) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(url + "/{id}", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> get(String Url) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(Url)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> delete(String url, Long id) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(url, id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> delete(String url, Long id, Map<String, String> params) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param(params.get("key"), params.get("value"))
                .when()
                .delete(url, id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> update(String url, Long id, Map<String, String> params) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when()
                .put(url, id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> post(String url, Long id, Map<String, String> params) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when()
                .post(url, id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> post(String url, Map<String, String> params) {
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(url)
                .then().log().all()
                .extract();
    }
}
