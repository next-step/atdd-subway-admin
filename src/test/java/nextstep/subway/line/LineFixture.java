package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

public class LineFixture {
    private LineFixture() {
    }

    public static ExtractableResponse<Response> requestCreateLine(Map<String, String> params) {
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> requestUpdateLine(Long id, Map<String, String> params2) {
        return RestAssured
                .given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/" + id)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> requestGetLines() {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> requestGetLineById(Long id) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/" + id)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> requestDeleteLine(Long id) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/" + id)
                .then().log().all().extract();
    }

    public static LineResponse ofLineResponse(ExtractableResponse<Response> createdResponse) {
        return createdResponse.jsonPath().getObject(".", LineResponse.class);
    }

    public static List<LineResponse> ofLineResponses(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class);
    }
}
