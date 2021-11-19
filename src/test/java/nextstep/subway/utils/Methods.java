package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.Map;

public class Methods {
    public static ExtractableResponse<Response> get(String url) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(url)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> put(String url, Map<String, String> toBeParams) {
        return RestAssured
                .given().log().all()
                .body(toBeParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(url)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> delete(String url) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(url)
                .then().log().all().extract();
    }
}
