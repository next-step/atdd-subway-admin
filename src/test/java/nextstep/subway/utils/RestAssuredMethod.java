package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Map;
import org.springframework.http.MediaType;

public class RestAssuredMethod {

    private RestAssuredMethod() {
    }

    public static ExtractableResponse<Response> post(String path, Object params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> get(String path, Map<String, ?> pathParams) {
        return RestAssured.given().log().all()
                .when().get(path, pathParams)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> get(String path) {
        return RestAssured.given().log().all()
                .when().get(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> put(String path, Map<String, ?> pathParams,
                                                    Object params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(path, pathParams)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> delete(String path, Map<String, ?> pathParams) {
        return RestAssured.given().log().all()
                .when().delete(path, pathParams)
                .then().log().all()
                .extract();
    }
}
