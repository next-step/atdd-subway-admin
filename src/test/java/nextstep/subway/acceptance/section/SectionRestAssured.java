package nextstep.subway.acceptance.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;

public class SectionRestAssured {
    public static ExtractableResponse<Response> 지하철구간_추가_요청(long lineId, long upStationId, long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post( "/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철구간_제거_요청(long lineId, long stationId) {
        Map<String, Object> params = new HashMap<>();
        params.put("lineId", lineId);
        params.put("stationId", stationId);

        return RestAssured.given().log().all()
                .param("stationId", stationId)
                .when().delete( "/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();
    }
}
