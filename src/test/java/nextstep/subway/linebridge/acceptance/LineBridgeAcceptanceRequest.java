package nextstep.subway.linebridge.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.line.acceptance.LineAcceptance.*;
import static nextstep.subway.station.acceptance.StationAcceptaneRequest.*;
import static org.assertj.core.api.Assertions.assertThat;

public class LineBridgeAcceptanceRequest {
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

    public static void 두개의_구간을_생성(){
        String upStationId = "1";
        String downStationId = "3";
        int distance = 2;

        지하철역과_노선_존재();
        지하철구간_존재(downStationId, upStationId, distance);
    }

    public static ExtractableResponse<Response> 구간을_삭제한다(Long lindId, Long stationId) {
        return RestAssured
                .given().log().all()
                .param("stationId", stationId)
                .when().delete("/lines/" + lindId + "/sections")
                .then().log().all()
                .extract();
    }

    public static void 구간이_삭제된다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 구간이_삭제되지_않는다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
