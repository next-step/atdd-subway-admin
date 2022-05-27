package nextstep.subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.Map;

import org.springframework.http.MediaType;

public final class RestAssuredTemplate {
    public static ExtractableResponse<Response> sendPost(String path, Map<String, Object> params) {
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> sendGet(String path) {
        return RestAssured
                .given().log().all()
                .when().get(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> sendGetWithId(String path, Long id) {
        return RestAssured
                .given().pathParam("id", id).log().all()
                .when().get(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> sendDelete(String path, Long id) {
        return RestAssured
                .given().pathParam("id", id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .log().all()
                .when().delete(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> sendPut(String path, String pathParamValue, Map<String, Object> params) {
        return RestAssured
                .given().pathParam("id", pathParamValue)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .log().all()
                .when().put(path)
                .then().log().all()
                .extract();
    }
}
