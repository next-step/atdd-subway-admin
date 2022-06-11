package nextstep.subway.section.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.line.accecptance.LineAcceptanceRequest.지하철노선_존재;
import static nextstep.subway.station.StationRequest.지하철역_존재;

public class SectionAcceptanceRequest {
    public static void 지하철역과_노선_존재() {
        지하철역_존재("강남역");
        지하철역_존재("잠실역");
        지하철역_존재("역삼역");
        지하철역_존재("석수역");
        지하철역_존재("관악역");
        지하철노선_존재("2호선", 1L, 2L, 10, "green");
    }

    public static ExtractableResponse<Response> 지하철구간_생성_요청(String upStationId, String downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("downStationId", downStationId);
        params.put("upStationId", upStationId);
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/lines/1/sections")
                .then().log().all()
                .extract();
    }

    public static void 지하철구간_존재(String downStationId, String upStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("downStationId", downStationId);
        params.put("upStationId", upStationId);
        params.put("distance", distance);

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/lines/1/sections")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철구간_삭제_요청(String upStationId, String downStationId, int distance, String targetStationId) {
        Map<String, Object> params = new HashMap<>();
        params.put("downStationId", downStationId);
        params.put("upStationId", upStationId);
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().delete("/lines/1/sections?stationId=" + targetStationId)
                .then().log().all()
                .extract();
    }
}
