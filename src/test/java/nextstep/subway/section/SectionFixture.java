package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class SectionFixture {

    public static ExtractableResponse<Response> 구간_등록(final Long lineId, final Long upStationId,
        final Long downStationId, final int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines/" + lineId + "/sections")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 구간_제거(final Long lineId, final Long stationId) {
        Map<String, Object> params = new HashMap<>();
        params.put("stationId", stationId);
        return RestAssured.given().log().all()
            .params(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete("/lines/" + lineId + "/sections")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 구간_목록_조회(final Long lineId) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/lines/" + lineId + "/sections")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract();
    }

}
