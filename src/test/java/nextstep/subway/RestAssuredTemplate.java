package nextstep.subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.Map;

import org.springframework.http.MediaType;

public final class RestAssuredTemplate {
    public static ExtractableResponse<Response> get(String uri) {
        return RestAssured
                .given().log().all()
                .when().get(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> post(String uri, Map<String, Object> params) {
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> getWithId(String uri, Long id) {
        return RestAssured
                .given().pathParam("id", id).log().all()
                .when().get(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> delete(String uri) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> deleteWithId(String uri, Long id) {
        return RestAssured
                .given().pathParam("id", id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .log().all()
                .when().delete(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> putWithId(String uri, Long id, Map<String, Object> params) {
        return RestAssured
                .given().pathParam("id", id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .log().all()
                .when().put(uri)
                .then().log().all()
                .extract();
    }
}
