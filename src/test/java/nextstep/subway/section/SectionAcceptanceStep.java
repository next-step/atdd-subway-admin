package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SectionAcceptanceStep {
    public static final String RESOURCES = "/sections";
    public static final String UP_STATION_ID = "upStationId";
    public static final String DOWN_STATION_ID = "downStationId";
    public static final String DISTANCE = "distance";

    public static ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(Long lineId, Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put(UP_STATION_ID, String.valueOf(upStationId));
        params.put(DOWN_STATION_ID, String.valueOf(downStationId));
        params.put(DISTANCE, String.valueOf(distance));
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/" + lineId + RESOURCES)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철역_등록되어_있음(Long lineId, Long upStationId, Long downStationId, int distance) {
        return 지하철_노선에_지하철역_등록_요청(lineId, upStationId, downStationId, distance);
    }

    public static void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 지하철_노선에_정렬된_지하철역_목록_포함됨(List<StationResponse> expected, List<StationResponse> actual) {
        assertAll(() -> {
            assertThat(actual.size()).isEqualTo(expected.size());
            for (int i = 0; i < actual.size(); i++) {
                actual.get(i).equals(expected.get(i));
            }
        });
    }
}
