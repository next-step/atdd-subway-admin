package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Map;
import org.springframework.http.MediaType;

public abstract class AcceptanceTest {
    protected ExtractableResponse<Response> sendGet(String path, Object... pathParams) {
        return RestAssured.given().log().all()
                .when().get(path, pathParams)
                .then().log().all()
                .extract();
    }

    protected ExtractableResponse<Response> sendPost(Map<String, Object> bodyParams, String path,
                                                     Object... pathParams) {
        return RestAssured.given().log().all()
                .body(bodyParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(path, pathParams)
                .then().log().all()
                .extract();
    }

    protected ExtractableResponse<Response> sendPut(Map<String, Object> bodyParams, String path,
                                                    Object... pathParams) {
        return RestAssured.given().log().all()
                .body(bodyParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(path, pathParams)
                .then().log().all()
                .extract();
    }

    protected ExtractableResponse<Response> sendDelete(String path, Object... pathParams) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(path, pathParams)
                .then().log().all()
                .extract();
    }
}
