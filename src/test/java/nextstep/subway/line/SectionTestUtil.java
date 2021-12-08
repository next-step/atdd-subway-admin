package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.dto.StationResponse;

public class SectionTestUtil {

    public static SectionRequest 구간_요청_등록_정보(Long upStationId, Long downStationId, int distance) {
        return new SectionRequest(upStationId, downStationId, distance);
    }

    public static ExtractableResponse<Response> 노선_구간_등록_요청(Long lineId, SectionRequest sectionRequest) {
        return RestAssured
            .given().log().all()
            .body(sectionRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post(LineTestUtil.LINES_PATH + "{lineId}/sections", lineId)
            .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 노선_구간_삭제_요청(Long lineId, Long stationId) {
        return RestAssured
            .given().log().all()
            .param("stationId", stationId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete(LineTestUtil.LINES_PATH + "{lineId}/sections", lineId)
            .then().log().all().extract();
    }

    public static void 지하철_노선에_삭제된_지하철역이_포함되지_않음(ExtractableResponse<Response> response, Long stationId) {
        boolean contains = response.jsonPath()
            .getList("stations", StationResponse.class)
            .stream()
            .anyMatch(it -> it.getId().equals(stationId));
        assertThat(contains).isFalse();
    }

    public static void 지하철_노선에_포함된_지하철역_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static void 지하철_노선에_포함되지_않은_지하철역_삭제_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getString("message")).isEqualTo("지하철역이 노선에 포함되어 있지 않습니다.");
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철_노선의_마지막_남은_구간_삭제_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getString("message")).isEqualTo("더 이상 삭제할 수 없습니다.");
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

    }

    public static void 새로운역_상행_종점으로_등록됨(ExtractableResponse<Response> response, Long stationId) {
        List<StationResponse> stations = response.jsonPath().getList("stations", StationResponse.class);
        Long upStationId = stations.get(0).getId();
        assertThat(upStationId).isEqualTo(stationId);
    }

    public static void 새로운역_하행_종점으로_등록됨(ExtractableResponse<Response> response, Long stationId) {
        List<StationResponse> stations = response.jsonPath().getList("stations", StationResponse.class);
        Long downStationId = stations.get(stations.size() - 1).getId();
        assertThat(downStationId).isEqualTo(stationId);
    }

    public static void 역_사이에_새로운_역_등록됨(ExtractableResponse<Response> response, Long stationId) {
        List<StationResponse> stations = response.jsonPath().getList("stations", StationResponse.class);
        boolean isContains = stations.stream()
            .anyMatch(station -> stationId.equals(station.getId()));
        assertThat(isContains).isTrue();
    }

    public static void 지하철_노선_등록_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
