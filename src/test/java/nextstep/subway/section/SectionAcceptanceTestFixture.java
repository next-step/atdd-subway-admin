package nextstep.subway.section;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class SectionAcceptanceTestFixture {
    public static ExtractableResponse<Response> 지하철_구간_등록(Integer id, Map<String, String> params) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines/" + id + "/sections")
            .then().log().all()
            .extract();
    }

    public static Map<String, String> 구간_등록_요청_파라미터(Integer upStationId, Integer downStationId, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId.toString());
        params.put("downStationId", downStationId.toString());
        params.put("distance", distance);
        return params;
    }

    public static ExtractableResponse<Response> 지하철_구간_삭제(Integer id, Map<String, String> params) {
        return RestAssured.given().log().all()
            .params(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete("/lines/" + id + "/sections")
            .then().log().all()
            .extract();
    }

    public static Map<String, String> 구간_삭제_요청_파라미터(Integer stationId) {
        Map<String, String> params = new HashMap<>();
        params.put("stationId", stationId.toString());
        return params;
    }
}
