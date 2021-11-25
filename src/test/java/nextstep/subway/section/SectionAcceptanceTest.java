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

        ExtractableResponse<Response> upStationResponse = 지하철역_등록("강남");
        ExtractableResponse<Response> downStationResponse = 지하철역_등록("광교");
        ExtractableResponse<Response> addStationResponse = 지하철역_등록("신대방");

        long upStationId = extractId(upStationResponse);
        long downStationId = extractId(downStationResponse);
        long addDownStationId = extractId(addStationResponse);

        ExtractableResponse<Response> lineResponse = 지하철_노선_등록("신분당선", "bg-red-600", upStationId, downStationId, 7);
        long lineId = extractId(lineResponse);

        int distance = 4;
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", addDownStationId);
        params.put("distance", distance);

        ExtractableResponse<Response> response = ApiUtils.post(String.format("/lines/%s/sections", lineId), params);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.body().jsonPath().getInt("distance")).isEqualTo(distance);
    }

    @DisplayName("새로운 역을 상행 종점으로 하여 구간을 등록할 경우")
    @Test
    void addUpSectionTest() {

        ExtractableResponse<Response> upStationResponse = 지하철역_등록("강남");
        ExtractableResponse<Response> downStationResponse = 지하철역_등록("광교");
        ExtractableResponse<Response> addStationResponse = 지하철역_등록("신대방");

        long upStationId = extractId(upStationResponse);
        long downStationId = extractId(downStationResponse);

        ExtractableResponse<Response> lineResponse = 지하철_노선_등록("신분당선", "bg-red-600", upStationId, downStationId, 7);

        long lineId = extractId(lineResponse);
        long addStationId = extractId(addStationResponse);

        int distance = 4;

        Map<String, Object> params = new HashMap<>();
        params.put("downStationId", upStationId);
        params.put("upStationId", addStationId);
        params.put("distance", distance);

        ExtractableResponse<Response> response = ApiUtils.post(String.format("/lines/%s/sections", lineId), params);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.body().jsonPath().getInt("distance")).isEqualTo(distance);
    }

    @DisplayName("새로운 역을 하행 종점으로 하여 구간을 등록할 경우")
    @Test
    void addDownSectionTest() {

        ExtractableResponse<Response> upStationResponse = 지하철역_등록("강남");
        ExtractableResponse<Response> downStationResponse = 지하철역_등록("광교");
        ExtractableResponse<Response> addStationResponse = 지하철역_등록("신대방");

        long upStationId = extractId(upStationResponse);
        long downStationId = extractId(downStationResponse);

        ExtractableResponse<Response> lineResponse = 지하철_노선_등록("신분당선", "bg-red-600", upStationId, downStationId, 10);

        long lineId = extractId(lineResponse);
        long addStationId = extractId(addStationResponse);

        int distance = 3;
        Map<String, Object> params = new HashMap<>();
        params.put("downStationId", addStationId);
        params.put("upStationId", downStationId);
        params.put("distance", distance);

        ExtractableResponse<Response> response = ApiUtils.post(String.format("/lines/%s/sections", lineId), params);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.body().jsonPath().getInt("distance")).isEqualTo(distance);
    }

    @DisplayName("등록될 구간의 거리가 기존 구간 사이에 등록될 때 구간의 거리가 기존 구간 거리와 동일하거나 큰 경우 예외처리")
    @Test
    void addSection_DistanceGraterEqualExceptionTest() {

        ExtractableResponse<Response> upStationResponse = 지하철역_등록("강남");
        ExtractableResponse<Response> downStationResponse = 지하철역_등록("광교");

        long upStationId = extractId(upStationResponse);
        long downStationId = extractId(downStationResponse);

        ExtractableResponse<Response> lineResponse = 지하철_노선_등록("신분당선", "bg-red-600", upStationId, downStationId, 7);
        ExtractableResponse<Response> addUpStationResponse = 지하철역_등록("신대방");

        long lineId = lineResponse.body().jsonPath().getLong("id");
        long addUpStationId = extractId(addUpStationResponse);

        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", addUpStationId);
        params.put("downStationId", downStationId);
        params.put("distance", 7);

        ExtractableResponse<Response> response = ApiUtils.post(String.format("/lines/%s/sections", lineId), params);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("등록될 구간이 기존 구간과 동일한 상행, 하행을 등록할 경우 예외처리")
    @Test
    void addSection_EqualSectionExceptionTest() {

        ExtractableResponse<Response> upStationResponse = 지하철역_등록("강남");
        ExtractableResponse<Response> downStationResponse = 지하철역_등록("광교");

        long upStationId = extractId(upStationResponse);
        long downStationId = extractId(downStationResponse);

        ExtractableResponse<Response> lineResponse = 지하철_노선_등록("신분당선", "bg-red-600", upStationId, downStationId, 7);
        long lineId = extractId(lineResponse);

        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", 7);

        ExtractableResponse<Response> response = ApiUtils.post(String.format("/lines/%s/sections", lineId), params);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("등록될 구간이 상행과 하행역 둘 다 포함되어있지 않으면 예외처리")
    @Test
    void addSection_UpStationOrDownStation_NotContainsExceptionTest() {

        ExtractableResponse<Response> upStationResponse = 지하철역_등록("강남");
        ExtractableResponse<Response> downStationResponse = 지하철역_등록("광교");

        long upStationId = extractId(upStationResponse);
        long downStationId = extractId(downStationResponse);

        ExtractableResponse<Response> lineResponse = 지하철_노선_등록("신분당선", "bg-red-600", upStationId, downStationId, 7);
        long lineId = extractId(lineResponse);

        ExtractableResponse<Response> addUpStationResponse = 지하철역_등록("신대방");
        ExtractableResponse<Response> addDownStationResponse = 지하철역_등록("신림");

        long addUpStationId = extractId(addUpStationResponse);
        long addDownStationId = extractId(addDownStationResponse);

        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", addUpStationId);
        params.put("downStationId", addDownStationId);
        params.put("distance", 4);

        ExtractableResponse<Response> response = ApiUtils.post(String.format("/lines/%s/sections", lineId), params);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("구간 삭제 시 삭제되는 역이 종점일경우")
    @Test
    void removeSectionTest() {

        ExtractableResponse<Response> upStationResponse = 지하철역_등록("강남");
        ExtractableResponse<Response> downStationResponse = 지하철역_등록("광교");
        ExtractableResponse<Response> addStationResponse = 지하철역_등록("신대방");

        long upStationId = extractId(upStationResponse);
        long downStationId = extractId(downStationResponse);
        long addDownStationId = extractId(addStationResponse);

        ExtractableResponse<Response> lineResponse = 지하철_노선_등록("신분당선", "bg-red-600", upStationId, downStationId, 7);
        long lineId = extractId(lineResponse);

        int distance = 4;
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", addDownStationId);
        params.put("distance", distance);

        ApiUtils.post(String.format("/lines/%s/sections", lineId), params);

        ExtractableResponse<Response> response = ApiUtils.delete(String.format("/lines/%d/sections?stationId=%d", lineId, upStationId));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("구간 삭제 시 삭제하는 역이 중간역일경우")
    @Test
    void removeSection() {

        ExtractableResponse<Response> upStationResponse = 지하철역_등록("강남");
        ExtractableResponse<Response> downStationResponse = 지하철역_등록("광교");
        ExtractableResponse<Response> addStationResponse = 지하철역_등록("신대방");

        long upStationId = extractId(upStationResponse);
        long downStationId = extractId(downStationResponse);
        long addDownStationId = extractId(addStationResponse);

        ExtractableResponse<Response> lineResponse = 지하철_노선_등록("신분당선", "bg-red-600", upStationId, downStationId, 7);
        long lineId = extractId(lineResponse);

        int distance = 4;
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", addDownStationId);
        params.put("distance", distance);

        ApiUtils.post(String.format("/lines/%s/sections", lineId), params);

        ExtractableResponse<Response> response = ApiUtils.delete(String.format("/lines/%d/sections?stationId=%d", lineId, addDownStationId));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("구간 삭제 시 일치하는 상행, 하행이 없을경우 예외처리")
    @Test
    void removeSection_NotCorrectStationExceptionTest() {

        ExtractableResponse<Response> upStationResponse = 지하철역_등록("강남");
        ExtractableResponse<Response> downStationResponse = 지하철역_등록("광교");
        ExtractableResponse<Response> removeStationResponse = 지하철역_등록("신대방");

        long upStationId = extractId(upStationResponse);
        long downStationId = extractId(downStationResponse);
        long removeStationId = extractId(removeStationResponse);

        ExtractableResponse<Response> lineResponse = 지하철_노선_등록("신분당선", "bg-red-600", upStationId, downStationId, 7);
        long lineId = extractId(lineResponse);

        ExtractableResponse<Response> response = ApiUtils.delete(String.format("/lines/%d/sections?stationId=%d", lineId, removeStationId));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("구간이 하나일 때 구간 삭제 예외처리")
    @Test
    void removeSection_NotExistExceptionTest() {

        ExtractableResponse<Response> upStationResponse = 지하철역_등록("강남");
        ExtractableResponse<Response> downStationResponse = 지하철역_등록("광교");

        long upStationId = extractId(upStationResponse);
        long downStationId = extractId(downStationResponse);

        ExtractableResponse<Response> lineResponse = 지하철_노선_등록("신분당선", "bg-red-600", upStationId, downStationId, 7);
        long lineId = extractId(lineResponse);

        ExtractableResponse<Response> response = ApiUtils.delete(String.format("/lines/%d/sections?stationId=%d", lineId, upStationId));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("구간 삭제 시 삭제하는 역이 종점일경우")
    @Test
    void removeSection_UpOrDownStationTest() {

        Line line = new Line("신분당선", "bg-red-600");
        Station upStation = stationRepository.save(new Station("광교"));
        Station downStation = stationRepository.save(new Station("강남"));
        Station removeStation = stationRepository.save(new Station("대림"));
        Integer distance = 10;
        line.addSection(upStation, downStation, distance);
        line.addSection(downStation, removeStation, 10);
        lineRepository.save(line);

        boolean isRemove = line.removeSection(removeStation);
        lineRepository.flush();

        assertThat(isRemove).isTrue();
        assertThat(line.getSections().size()).isEqualTo(1);
    }

    @DisplayName("구간 삭제 시 삭제하는 역이 중간역일경우")
    @Test
    void removeSection_MiddleStationTest() {

        Line line = new Line("신분당선", "bg-red-600");
        Station upStation = stationRepository.save(new Station("광교"));
        Station downStation = stationRepository.save(new Station("강남"));
        Station removeStation = stationRepository.save(new Station("대림"));
        Integer distance = 10;
        line.addSection(upStation, downStation, distance);
        line.addSection(downStation, removeStation, 7);
        lineRepository.save(line);

        boolean isRemove = line.removeSection(downStation);
        lineRepository.flush();

        assertThat(isRemove).isTrue();
        assertThat(line.getSections().size()).isEqualTo(1);
    }

    @DisplayName("구간 삭제 시 일치하는 역이 없을경우 예외처리")
    @Test
    void removeSection_NotCorrectStationTest() {

        Line line = new Line("신분당선", "bg-red-600");
        Station upStation = stationRepository.save(new Station("광교"));
        Station downStation = stationRepository.save(new Station("강남"));
        Station removeStation = stationRepository.save(new Station("대림"));
        Integer distance = 10;
        line.addSection(upStation, downStation, distance);
        lineRepository.save(line);

        assertThatThrownBy(() -> line.removeSection(removeStation)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구간 삭제 시 구간이 존재하지 않을경우 예외처리")
    @Test
    void removeSectionIfRemoveNotExistTest() {

        Line line = new Line("신분당선", "bg-red-600");
        Station upStation = stationRepository.save(new Station("광교"));
        Station downStation = stationRepository.save(new Station("강남"));
        Integer distance = 10;
        line.addSection(upStation, downStation, distance);
        lineRepository.save(line);

        assertThatThrownBy(() -> line.removeSection(upStation)).isInstanceOf(IllegalArgumentException.class);
    }

}
