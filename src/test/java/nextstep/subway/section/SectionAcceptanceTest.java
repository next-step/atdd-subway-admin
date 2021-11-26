package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.utils.ApiUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.line.LineAcceptanceTest.지하철_노선_등록;
import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationRepository stationRepository;

    @DisplayName("기존 상행 하행 사이에 구간 등록할 경우")
    @Test
    void addSectionTest() {

        ExtractableResponse<Response> 강남역 = 지하철역_등록("강남");
        ExtractableResponse<Response> 광교역 = 지하철역_등록("광교");
        ExtractableResponse<Response> 신대방역 = 지하철역_등록("신대방");

        long 강남역Id = extractId(강남역);
        long 광교역Id = extractId(광교역);
        long 신대방역Id = extractId(신대방역);

        ExtractableResponse<Response> 신분당선 = 지하철_노선_등록("신분당선", "bg-red-600", 강남역Id, 광교역Id, 7);
        long lineId = extractId(신분당선);

        int distance = 4;
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", 강남역Id);
        params.put("downStationId", 신대방역Id);
        params.put("distance", distance);

        ExtractableResponse<Response> response = ApiUtils.post(String.format("/lines/%s/sections", lineId), params);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.body().jsonPath().getInt("distance")).isEqualTo(distance);
    }

    @DisplayName("새로운 역을 상행 종점으로 하여 구간을 등록할 경우")
    @Test
    void addUpSectionTest() {

        ExtractableResponse<Response> 강남역 = 지하철역_등록("강남");
        ExtractableResponse<Response> 광교역 = 지하철역_등록("광교");
        ExtractableResponse<Response> 신대방역 = 지하철역_등록("신대방");

        long 강남역Id = extractId(강남역);
        long 광교역Id = extractId(광교역);

        ExtractableResponse<Response> 신분당선 = 지하철_노선_등록("신분당선", "bg-red-600", 강남역Id, 광교역Id, 7);

        long 신분당선Id = extractId(신분당선);
        long 신대방역Id = extractId(신대방역);

        int distance = 4;

        Map<String, Object> params = new HashMap<>();
        params.put("downStationId", 강남역Id);
        params.put("upStationId", 신대방역Id);
        params.put("distance", distance);

        ExtractableResponse<Response> response = ApiUtils.post(String.format("/lines/%s/sections", 신분당선Id), params);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.body().jsonPath().getInt("distance")).isEqualTo(distance);
    }

    @DisplayName("새로운 역을 하행 종점으로 하여 구간을 등록할 경우")
    @Test
    void addDownSectionTest() {

        ExtractableResponse<Response> 강남역 = 지하철역_등록("강남");
        ExtractableResponse<Response> 광교역 = 지하철역_등록("광교");
        ExtractableResponse<Response> 신대방역 = 지하철역_등록("신대방");

        long 강남역Id = extractId(강남역);
        long 광교역Id = extractId(광교역);

        ExtractableResponse<Response> 신분당선 = 지하철_노선_등록("신분당선", "bg-red-600", 강남역Id, 광교역Id, 10);

        long 신분당선Id = extractId(신분당선);
        long 신대방역Id = extractId(신대방역);

        int distance = 3;
        Map<String, Object> params = new HashMap<>();
        params.put("downStationId", 신대방역Id);
        params.put("upStationId", 광교역Id);
        params.put("distance", distance);

        ExtractableResponse<Response> response = ApiUtils.post(String.format("/lines/%s/sections", 신분당선Id), params);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.body().jsonPath().getInt("distance")).isEqualTo(distance);
    }

    @DisplayName("등록될 구간의 거리가 기존 구간 사이에 등록될 때 구간의 거리가 기존 구간 거리와 동일하거나 큰 경우 예외처리")
    @Test
    void addSection_DistanceGraterEqualExceptionTest() {

        ExtractableResponse<Response> 강남역 = 지하철역_등록("강남");
        ExtractableResponse<Response> 광교역 = 지하철역_등록("광교");

        long 강남역Id = extractId(강남역);
        long 광교역Id = extractId(광교역);

        ExtractableResponse<Response> 신분당선 = 지하철_노선_등록("신분당선", "bg-red-600", 강남역Id, 광교역Id, 7);
        ExtractableResponse<Response> 신대방역 = 지하철역_등록("신대방");

        long 신분당선Id = extractId(신분당선);
        long 신대방역Id = extractId(신대방역);

        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", 신대방역Id);
        params.put("downStationId", 광교역Id);
        params.put("distance", 7);

        ExtractableResponse<Response> response = ApiUtils.post(String.format("/lines/%s/sections", 신분당선Id), params);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("등록될 구간이 기존 구간과 동일한 상행, 하행을 등록할 경우 예외처리")
    @Test
    void addSection_EqualSectionExceptionTest() {

        ExtractableResponse<Response> 강남역 = 지하철역_등록("강남");
        ExtractableResponse<Response> 광교역 = 지하철역_등록("광교");

        long 강남역Id = extractId(강남역);
        long 광교역Id = extractId(광교역);

        ExtractableResponse<Response> 신분당선 = 지하철_노선_등록("신분당선", "bg-red-600", 강남역Id, 광교역Id, 7);
        long 신분당선Id = extractId(신분당선);

        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", 강남역Id);
        params.put("downStationId", 광교역Id);
        params.put("distance", 7);

        ExtractableResponse<Response> response = ApiUtils.post(String.format("/lines/%s/sections", 신분당선Id), params);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("등록될 구간이 상행과 하행역 둘 다 포함되어있지 않으면 예외처리")
    @Test
    void addSection_UpStationOrDownStation_NotContainsExceptionTest() {

        ExtractableResponse<Response> 강남역 = 지하철역_등록("강남");
        ExtractableResponse<Response> 광교역 = 지하철역_등록("광교");

        long 강남역Id = extractId(강남역);
        long 광교역Id = extractId(광교역);

        ExtractableResponse<Response> 신분당선 = 지하철_노선_등록("신분당선", "bg-red-600", 강남역Id, 광교역Id, 7);
        long 신분당선Id = extractId(신분당선);

        ExtractableResponse<Response> 신대방역 = 지하철역_등록("신대방");
        ExtractableResponse<Response> 신림역 = 지하철역_등록("신림");

        long 신대방역Id = extractId(신대방역);
        long 신림역Id = extractId(신림역);

        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", 신대방역Id);
        params.put("downStationId", 신림역Id);
        params.put("distance", 4);

        ExtractableResponse<Response> response = ApiUtils.post(String.format("/lines/%s/sections", 신분당선Id), params);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("구간 삭제 시 삭제되는 역이 종점일경우")
    @Test
    void removeSectionTest() {

        ExtractableResponse<Response> 강남역 = 지하철역_등록("강남");
        ExtractableResponse<Response> 광교역 = 지하철역_등록("광교");
        ExtractableResponse<Response> 신대방역 = 지하철역_등록("신대방");

        long 강남역Id = extractId(강남역);
        long 광교역Id = extractId(광교역);
        long 신대방역Id = extractId(신대방역);

        ExtractableResponse<Response> 신분당선 = 지하철_노선_등록("신분당선", "bg-red-600", 강남역Id, 광교역Id, 7);
        long 신분당선Id = extractId(신분당선);

        int distance = 4;
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", 강남역Id);
        params.put("downStationId", 신대방역Id);
        params.put("distance", distance);

        ApiUtils.post(String.format("/lines/%s/sections", 신분당선Id), params);

        ExtractableResponse<Response> response = ApiUtils.delete(String.format("/lines/%d/sections?stationId=%d", 신분당선Id, 강남역Id));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("구간 삭제 시 삭제하는 역이 중간역일경우")
    @Test
    void removeSection() {

        ExtractableResponse<Response> 강남역 = 지하철역_등록("강남");
        ExtractableResponse<Response> 광교역 = 지하철역_등록("광교");
        ExtractableResponse<Response> 신대방역 = 지하철역_등록("신대방");

        long 강남역Id = extractId(강남역);
        long 광교역Id = extractId(광교역);
        long 신대방역Id = extractId(신대방역);

        ExtractableResponse<Response> lineResponse = 지하철_노선_등록("신분당선", "bg-red-600", 강남역Id, 광교역Id, 7);
        long lineId = extractId(lineResponse);

        int distance = 4;
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", 강남역Id);
        params.put("downStationId", 신대방역Id);
        params.put("distance", distance);

        ApiUtils.post(String.format("/lines/%s/sections", lineId), params);

        ExtractableResponse<Response> response = ApiUtils.delete(String.format("/lines/%d/sections?stationId=%d", lineId, 신대방역Id));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("구간 삭제 시 일치하는 상행, 하행이 없을경우 예외처리")
    @Test
    void removeSection_NotCorrectStationExceptionTest() {

        ExtractableResponse<Response> 강남역 = 지하철역_등록("강남");
        ExtractableResponse<Response> 광교역 = 지하철역_등록("광교");
        ExtractableResponse<Response> 신대방역 = 지하철역_등록("신대방");

        long 강남역Id = extractId(강남역);
        long 광교역Id = extractId(광교역);
        long 신대방역Id = extractId(신대방역);

        ExtractableResponse<Response> 신분당선 = 지하철_노선_등록("신분당선", "bg-red-600", 강남역Id, 광교역Id, 7);
        long lineId = extractId(신분당선);

        ExtractableResponse<Response> response = ApiUtils.delete(String.format("/lines/%d/sections?stationId=%d", lineId, 신대방역Id));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("구간이 하나일 때 구간 삭제 예외처리")
    @Test
    void removeSection_NotExistExceptionTest() {

        ExtractableResponse<Response> 강남역 = 지하철역_등록("강남");
        ExtractableResponse<Response> 광교역 = 지하철역_등록("광교");

        long 강남역Id = extractId(강남역);
        long 광교역Id = extractId(광교역);

        ExtractableResponse<Response> 신분당선 = 지하철_노선_등록("신분당선", "bg-red-600", 강남역Id, 광교역Id, 7);
        long 신분당선Id = extractId(신분당선);

        ExtractableResponse<Response> response = ApiUtils.delete(String.format("/lines/%d/sections?stationId=%d", 신분당선Id, 강남역Id));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("구간 삭제 시 삭제하는 역이 종점일경우")
    @Test
    void removeSection_UpOrDownStationTest() {

        Line line = new Line("신분당선", "bg-red-600");
        Station 광교역 = stationRepository.save(new Station("광교"));
        Station 강남역 = stationRepository.save(new Station("강남"));
        Station 대림역 = stationRepository.save(new Station("대림"));
        Integer distance = 10;
        line.addSection(광교역, 강남역, distance);
        line.addSection(강남역, 대림역, 10);
        lineRepository.save(line);

        boolean isRemove = line.removeSection(대림역);
        lineRepository.flush();

        assertThat(isRemove).isTrue();
        assertThat(line.getSections().size()).isEqualTo(1);
    }

    @DisplayName("구간 삭제 시 삭제하는 역이 중간역일경우")
    @Test
    void removeSection_MiddleStationTest() {

        Line line = new Line("신분당선", "bg-red-600");
        Station 광교역 = stationRepository.save(new Station("광교"));
        Station 강남역 = stationRepository.save(new Station("강남"));
        Station 대림역 = stationRepository.save(new Station("대림"));
        Integer distance = 10;
        line.addSection(광교역, 강남역, distance);
        line.addSection(강남역, 대림역, 7);
        lineRepository.save(line);

        boolean isRemove = line.removeSection(강남역);
        lineRepository.flush();

        assertThat(isRemove).isTrue();
        assertThat(line.getSections().size()).isEqualTo(1);
    }

    @DisplayName("구간 삭제 시 일치하는 역이 없을경우 예외처리")
    @Test
    void removeSection_NotCorrectStationTest() {

        Line line = new Line("신분당선", "bg-red-600");
        Station 광교역 = stationRepository.save(new Station("광교"));
        Station 강남역 = stationRepository.save(new Station("강남"));
        Station 대림역 = stationRepository.save(new Station("대림"));
        Integer distance = 10;
        line.addSection(광교역, 강남역, distance);
        lineRepository.save(line);

        assertThatThrownBy(() -> line.removeSection(대림역)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구간 삭제 시 구간이 존재하지 않을경우 예외처리")
    @Test
    void removeSectionIfRemoveNotExistTest() {

        Line line = new Line("신분당선", "bg-red-600");
        Station 광교역 = stationRepository.save(new Station("광교"));
        Station 강남역 = stationRepository.save(new Station("강남"));
        Integer distance = 10;
        line.addSection(광교역, 강남역, distance);
        lineRepository.save(line);

        assertThatThrownBy(() -> line.removeSection(광교역)).isInstanceOf(IllegalArgumentException.class);
    }

}
