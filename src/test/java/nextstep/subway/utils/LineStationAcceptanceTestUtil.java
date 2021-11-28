package nextstep.subway.utils;


import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class LineStationAcceptanceTestUtil {

    public static final int BASE_DISTANCE = 100;
    public static int SAFE_DISTANCE = 50;
    public static int OVER_DISTANCE = 200;

    private LineStationAcceptanceTestUtil() {
    }

    public static void 지하철_구간_요청_응답_검증(ExtractableResponse<Response> response,
        HttpStatus expected) {
        assertThat(response.statusCode()).isEqualTo(expected.value());
    }

    public static ExtractableResponse<Response> 지하철_노선구간_추가_요청(Long lineId,
        Map<String, String> params) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/" + lineId + "/sections")
            .then().log().all()
            .extract();
    }


    public static ExtractableResponse<Response> 지하철_노선구간_추가_되어_있음(Long lineId, Long stationId,
        Long nextStationId, int distance) {
        Map<String, String> params = 지하철_노선_구간_추가_파라미터_맵핑(stationId, nextStationId, distance);
        return 지하철_노선구간_추가_요청(lineId, params);
    }

    public static void 지하철_노선에_지하철역_순서_정렬됨(ExtractableResponse<Response> response,
        List<Long> expectedStationIds) {
        LineResponse line = response.as(LineResponse.class);

        assertThat(line.getStations()).extracting("id")
            .containsExactlyElementsOf(expectedStationIds);
    }

    public static Map<String, String> 지하철_노선_구간_추가_파라미터_맵핑(Long stationId, Long nextStationId,
        int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(stationId));
        params.put("downStationId", String.valueOf(nextStationId));
        params.put("distance", String.valueOf(distance));
        return params;
    }

    public static void 노선_사이_역_추가_거리_마이너스_검증(ExtractableResponse<Response> lineListResponse,
        Integer... expectedDistances) {
        List<Integer> distances = lineListResponse.jsonPath().getList("stations.distance");
        assertThat(distances).containsExactly(expectedDistances);
    }

    public static ExtractableResponse<Response> 지하철_노선구간_제거_됨(Long lineId, Long removeStationId) {
        return RestAssured.given().log().all()
            .when()
            .delete("/lines/" + lineId + "/sections?stationId=" + removeStationId)
            .then().log().all()
            .extract();
    }
}
