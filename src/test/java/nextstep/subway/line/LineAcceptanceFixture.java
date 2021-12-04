package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
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

    public static Map<String, String> createParams(StationResponse upStation, StationResponse downStation, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStation.getId().toString());
        params.put("downStationId", downStation.getId().toString());
        params.put("distance", String.valueOf(distance));
        return params;
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

    public static ExtractableResponse<Response> 노선_생성을_요청한다(String name, String color) {
        Map<String, String> params = createParams(name, color);

        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 노선_생성을_요청한다(StationResponse upStation, StationResponse downStation, int distance, String lineName, String colorName) {
        Map<String, String> params = createParams(lineName, colorName, upStation.getId(), downStation.getId(), distance);

        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 노선목록_조회를_요청한다() {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 노선을_조회를_요청한다(Long id) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/" + id)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 노선_삭제를_요청한다(Long id) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/" + id)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 구간_추가를_요청한다(Long lineId, StationResponse upStation, StationResponse downStation, int distance) {
        Map<String, String> params = createParams(upStation, downStation, distance);
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/lines/" + lineId.toString() + "/sections")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 구간_삭제를_요청한다(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("stationId", stationId)
                .when().delete("/lines/" + lineId.toString() + "/sections")
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
