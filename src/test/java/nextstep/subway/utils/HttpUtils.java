package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import nextstep.subway.line.dto.LineRequest;
import org.springframework.http.MediaType;

public class HttpUtils {

    public static LineRequest getLineRequest(String name, String color) {
        return new LineRequest(name, color);
    }

    public static ExtractableResponse<Response> post(String path, Object request) {
        return response(request(request).post(path));
    }

    public static ExtractableResponse<Response> get(String path, Object... params) {
        return response(request().get(path, params));
    }

    private static RequestSpecification request(Object body) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(body);
    }

    private static RequestSpecification request() {
        return RestAssured.given().log().all().when();
    }

    private static ExtractableResponse<Response> response(Response response) {
        return response.then().log().all().extract();
    }

}
