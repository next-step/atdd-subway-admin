package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Map;
import nextstep.subway.common.exception.ErrorResponse;
import org.springframework.http.MediaType;

public class CommonMethod {

    public static ExtractableResponse<Response> create(Map<String, Object> params, String path) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(path)
            .then().log().all()
            .extract();
    }


    public static ExtractableResponse<Response> get(String path) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get(path)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> update(Map<String, Object> params, String path) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(path)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> delete(String path) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete(path)
            .then().log().all()
            .extract();
    }


    public static ErrorResponse getError(ExtractableResponse<Response> response) {
        return response.response().jsonPath().getObject(".", ErrorResponse.class);
    }


}
