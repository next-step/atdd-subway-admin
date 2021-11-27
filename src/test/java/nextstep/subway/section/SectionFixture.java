package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class SectionFixture {
    private SectionFixture() {

    }

    public static Map<String, String> createParams(StationResponse upStation, StationResponse downStation, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStation.getId().toString());
        params.put("downStationId", downStation.getId().toString());
        params.put("distance", String.valueOf(distance));
        return params;
    }

    public static ExtractableResponse<Response> requestAddSection(Long lineId, Map<String, String> params) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/" + lineId.toString() + "/sections")
                .then().log().all().extract();
    }
}
