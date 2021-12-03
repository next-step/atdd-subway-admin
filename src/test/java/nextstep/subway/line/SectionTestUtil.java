package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;

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

    public static void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK);

    }

    public static void 새로운역_상행_종점으로_등록됨(ExtractableResponse<Response> response, Long stationId) {
        List<Station> stations = response.jsonPath().getList("stations", Station.class);
        Long upStationId = stations.get(0).getId();
        assertThat(upStationId).isEqualTo(stationId);
    }

    public static void 새로운역_하행_종점으로_등록됨(ExtractableResponse<Response> response, Long stationId) {
        List<Station> stations = response.jsonPath().getList("stations", Station.class);
        Long downStationId = stations.get(stations.size() - 1).getId();
        assertThat(downStationId).isEqualTo(stationId);
    }

    public static void 역_사이에_새로운_역_등록됨(ExtractableResponse<Response> response, Long stationId) {
        List<Station> stations = response.jsonPath().getList("stations", Station.class);
        boolean isContains = stations.stream()
            .anyMatch(station -> station.getId() == stationId);
        assertThat(isContains).isTrue();
    }

    public static void 지하철_노선_등록_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
