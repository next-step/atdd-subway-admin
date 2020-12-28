package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import org.springframework.http.MediaType;

public class LineAcceptanceTestRequest {

    private LineAcceptanceTestRequest() {
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color) {
        LineRequest request = new LineRequest(name, color);

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
        return response;
    }
}
