package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.ApiRequest;
import org.springframework.http.MediaType;

public class CommonTestApiClient {

    public static ExtractableResponse<Response> post(ApiRequest apiRequest, String uri) {
        return RestAssured
                .given().log().all()
                .body(apiRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> get(String uri) {
        return RestAssured
                .given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();
    }
}
