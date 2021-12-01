package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LineAcceptanceFixture {
    private LineAcceptanceFixture() {
    }

    public static Map<String, String> createParams(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return params;
    }

    public static ExtractableResponse<Response> requestCreateLine(String name, String color) {
        Map<String, String> params = createParams(name, color);

        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    public static Map<String, String> createParams(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId.toString());
        params.put("downStationId", downStationId.toString());
        params.put("distance", String.valueOf(distance));
        return params;
    }

    public static ExtractableResponse<Response> requestCreateLine(Map<String, String> params) {
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
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
        return createdResponse.jsonPath()
                .getObject(".", LineResponse.class);
    }

    public static List<LineResponse> ofLineResponses(ExtractableResponse<Response> response) {
        return response.jsonPath()
                .getList(".", LineResponse.class);
    }

    @SafeVarargs
    public static List<LineResponse> ofLineResponses(ExtractableResponse<Response>... createdResponses) {
        return Arrays.stream(createdResponses)
                .map(LineAcceptanceFixture::ofLineResponse)
                .collect(Collectors.toList());
    }
}
