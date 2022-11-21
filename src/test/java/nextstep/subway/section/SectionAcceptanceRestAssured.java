package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;

public class SectionAcceptanceRestAssured {

    public static ExtractableResponse<Response> 지하철구간_추가(Long lineId, Long upStationId, Long downStationId, Integer distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철구간_제거(Long lineId, Long stationId) {
        Map<String, Object> params = new HashMap<>();
        params.put("stationId", stationId);

        return RestAssured.given().log().all()
                .queryParams(params)
                .when().delete("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();
    }
}
