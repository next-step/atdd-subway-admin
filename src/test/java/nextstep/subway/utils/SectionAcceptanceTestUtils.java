package nextstep.subway.utils;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class SectionAcceptanceTestUtils {
    public static final int SAFE_DISTANCE = 5;
    public static final int BASE_DISTANCE = 10;
    public static final int OVER_DISTANCE = 15;

    public static ExtractableResponse<Response> 지하철노선에_지하철역을_등록한다(Long lineId, Long upStationId, Long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return given().log().all()
            .pathParam("id", lineId)
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines/{id}/sections")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철노선_내_지하철역을_삭제한다(Long lineId, Long stationId) {
        return given().log().all()
            .param("stationId", stationId)
            .pathParam("id", lineId)
            .when().delete("/lines/{id}/sections")
            .then().log().all()
            .extract();
    }

    public static void 노선_새로운_지하철역_등록_성공_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 노선_새로운_지하철역_등록_실패_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 지하철노선_저장된_지하철역_목록_검증(ExtractableResponse<Response> response, String... stations) {
        assertThat(response.jsonPath().getList("stations.name")).isEqualTo(Arrays.asList(stations));
    }

    public static void 지하철노선_거리_검증(ExtractableResponse<Response> response, int distance) {
        assertThat(response.jsonPath().getInt("distance")).isEqualTo(distance);
    }
    public static void 지하철노선_저장된_지하철역_목록_순서_검증(ExtractableResponse<Response> response, String... stations) {
        assertThat(response.jsonPath().getList("stations.name")).containsExactly(stations);
    }

    public static void 노선_내_지하철역_삭제_성공_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK);
    }

    public static void 노선_내_지하철역_삭제_실패_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    public static void 지하철노선_포함되지_않은_지하철역_검증(ExtractableResponse<Response> response, String station) {
        assertThat(response.jsonPath().getList("stations.name")).doesNotContain(station);
    }
}