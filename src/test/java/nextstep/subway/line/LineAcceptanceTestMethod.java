package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import nextstep.subway.line.dto.LineRequest;
import org.springframework.http.MediaType;

public class LineAcceptanceTestMethod {

    public static LineRequest getLineRequest(String name, String color) {
        return new LineRequest(name, color);
    }

    public static ExtractableResponse<Response> getLinePostResponse(String path, LineRequest request) {
        return response(postRequest(request).post(path));

    }

    public static ExtractableResponse<Response> getLineGetResponse(String path) {
        return response(request().get(path));

    }

    private static RequestSpecification postRequest(Object body) {
        return RestAssured.given().log().all().body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE);
    }

    private static RequestSpecification request() {
        return RestAssured.given().log().all().when();
    }

    private static ExtractableResponse<Response> response(Response response) {
        return response.then().log().all().extract();
    }

}
