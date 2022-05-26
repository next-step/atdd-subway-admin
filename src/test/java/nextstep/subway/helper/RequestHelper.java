package nextstep.subway.helper;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.Map;

public class RequestHelper {
    private RequestHelper() {}

    public static ExtractableResponse<Response> getRequest(
            String path, Map<String, String> params, Object... pathVariables
    ) {
        return RestAssured.given().log().all()
                .params(params)
                .when().get(path, pathVariables)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> postRequest(
            String path, Map<String, String> params, Object body, Object... pathVariables
    ) {
        return RestAssured.given().log().all()
                .params(params)
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(path, pathVariables)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> deleteRequest(
            String path, Map<String, String> params, Object... pathVariables
    ) {
        return RestAssured.given().log().all()
                .params(params)
                .when().delete(path, pathVariables)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> putRequest(
            String path, Map<String, String> params, Object body, Object... pathVariables
    ) {
        return RestAssured.given().log().all()
                .params(params)
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(path, pathVariables)
                .then().log().all()
                .extract();
    }
}
