package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class SectionAcceptanceFixture {
    private SectionAcceptanceFixture() {

    }

    public static Map<String, String> createParams(StationResponse upStation, StationResponse downStation, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStation.getId().toString());
        params.put("downStationId", downStation.getId().toString());
        params.put("distance", String.valueOf(distance));
        return params;
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
}
