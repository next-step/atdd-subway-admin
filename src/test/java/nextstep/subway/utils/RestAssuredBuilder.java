package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class RestAssuredBuilder {

    private static final String CONTENT_TYPE = "Content-Type";

    private final Method method;
    private final String path;
    private String contentType;
    private Object body;

    public RestAssuredBuilder(Method method, String path) {
        this.method = method;
        this.path = path;
    }

    public RestAssuredBuilder setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public RestAssuredBuilder setBody(Object body) {
        this.body = body;
        return this;
    }

    public ExtractableResponse<Response> build() {
        Map<String, String> headers = new HashMap<>();
        if (this.contentType != null) {
            headers.put(CONTENT_TYPE, this.contentType);
        }

        if (this.body != null) {
            return generateExtractableResponse(headers);
        }

        return generateEmptyBodyExtractableResponse(headers);
    }

    private ExtractableResponse<Response> generateExtractableResponse(Map<String, String> headers) {
        return RestAssured.given()
                .log()
                .all()
                .when()
                .headers(headers)
                .body(this.body)
                .request(this.method, this.path)
                .then()
                .log()
                .all()
                .extract();
    }

    private ExtractableResponse<Response> generateEmptyBodyExtractableResponse(Map<String, String> headers) {
        return RestAssured.given()
                .log()
                .all()
                .when()
                .headers(headers)
                .request(this.method, this.path)
                .then()
                .log()
                .all()
                .extract();
    }
}
