package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.MediaType;

import java.util.function.BiFunction;

public class RequestTest {
    public static ExtractableResponse<Response> doPost(String url) {
        return doPost(url, null);
    }

    public static ExtractableResponse<Response> doPost(String url, Object body) {
        return doRequest(url, body, RequestSpecification::post);
    }

    public static ExtractableResponse<Response> doGet(String url) {
        return doGet(url, null);
    }

    public static ExtractableResponse<Response> doGet(String url, Object body) {
        return doRequest(url, body, RequestSpecification::get);
    }


    public static ExtractableResponse<Response> doPut(String url) {
        return doPut(url, null);
    }

    public static ExtractableResponse<Response> doPut(String url, Object body) {
        return doRequest(url, body, RequestSpecification::put);
    }


    public static ExtractableResponse<Response> doDelete(String url) {
        return doDelete(url, null);
    }

    public static ExtractableResponse<Response> doDelete(String url, Object body) {
        return doRequest(url, body, RequestSpecification::delete);
    }

    private static ExtractableResponse<Response> doRequest(String url, Object body
            , BiFunction<RequestSpecification, String, Response> biFunction) {
        RequestSpecification specification = RestAssured.given().log().all();
        if (body != null) {
            specification = specification.body(body)
                    .contentType(MediaType.APPLICATION_JSON_VALUE);
        }
        specification = specification.when();
        return biFunction.apply(specification, url)
                .then().log().all()
                .extract();
    }
}
