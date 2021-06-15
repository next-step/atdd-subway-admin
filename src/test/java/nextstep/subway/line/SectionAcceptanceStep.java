package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.common.error.ErrorResponse;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.line.application.LineQueryService.LINE_NAME_DUPLICATED_EXCEPTION_MESSAGE;
import static nextstep.subway.line.domain.Distance.BIGGER_THAN_DISTANCE_EXCEPTION_MESSAGE;
import static nextstep.subway.line.domain.Sections.EXISTS_SECTION_EXCEPTION_MESSAGE;
import static nextstep.subway.line.domain.Sections.NOT_EXISTS_ALL_STATIONS_EXCEPTION_MESSAGE;
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
                .when().post("/lines/" + lineId + RESOURCES)
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

    public static void 지하철_노선_구간_생성_요청_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철_노선_기존_구간_거리보다_커서_등록할수_없음(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getObject("", ErrorResponse.class).getMessage())
                .isEqualTo(BIGGER_THAN_DISTANCE_EXCEPTION_MESSAGE);
    }

    public static void 지하철_노선에_이미_지하철역_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getObject("", ErrorResponse.class).getMessage())
                .isEqualTo(EXISTS_SECTION_EXCEPTION_MESSAGE);
    }

    public static void 지하철_노선에_지하철역_존재하지않아_등록할수_없음(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getObject("", ErrorResponse.class).getMessage())
                .isEqualTo(NOT_EXISTS_ALL_STATIONS_EXCEPTION_MESSAGE);
    }
}
