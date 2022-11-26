package nextstep.subway.station.request;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class LineApi {
    public static ExtractableResponse<Response> createStationLine(String lineName, Long upStationId, Long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", lineName);
        params.put("color", "bg-red-600");
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", 10);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> getStationLine(long lineId) {
        return RestAssured.given().log().all()
                .when().get("/lines/{lineId}", lineId)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> getStationLines() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> updateStationLine(long stationLineId, Map<String, Object> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{stationLineId}", stationLineId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> deleteStationLine(Long lineId) {
        return RestAssured
                .given().log().all()
                .when().delete("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> deleteSection(Long lineId, Long stationId) {
        return RestAssured
                .given().log().all()
                .param("stationId", stationId)
                .when().delete("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();
    }
}
