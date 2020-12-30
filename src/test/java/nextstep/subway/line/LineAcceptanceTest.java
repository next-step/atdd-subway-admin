package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static nextstep.subway.line.LineAcceptanceTestRequest.*;
import static nextstep.subway.line.LineAcceptanceTestResponse.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @Autowired
    private StationService stationService;

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        StationResponse 강남역 = stationService.saveStation(new StationRequest("강남역"));
        StationResponse 역삼역 = stationService.saveStation(new StationRequest("역삼역"));
        LineRequest params = 지하철_노선_요청_생성("신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId(), 10L);

        // when
        ExtractableResponse<Response> response = 지하철_노선_등록되어_있음(params, "/lines");

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("지하철 노선을 생성할때 종점역(상행, 하행)을 함께 추가한다.")
    @Test
    void createLineWithStations() {
        // given
        StationResponse 강남역 = stationService.saveStation(new StationRequest("강남역"));
        StationResponse 역삼역 = stationService.saveStation(new StationRequest("역삼역"));
        LineRequest params = 지하철_노선_요청_생성("신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId(), 10L);

        // when
        ExtractableResponse<Response> response = 지하철_노선_등록되어_있음(params, "/lines");

        // then
        지하철_노선_생성됨(강남역.getId(), 역삼역.getId(), response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        StationResponse 강남역 = stationService.saveStation(new StationRequest("강남역"));
        StationResponse 역삼역 = stationService.saveStation(new StationRequest("역삼역"));
        LineRequest params = 지하철_노선_요청_생성("신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId(), 10L);
        지하철_노선_등록되어_있음(params, "/lines");

        // when
        ExtractableResponse<Response> response = 지하철_노선_등록되어_있음(params, "/lines");

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        StationResponse 강남역 = stationService.saveStation(new StationRequest("강남역"));
        StationResponse 역삼역 = stationService.saveStation(new StationRequest("역삼역"));
        LineRequest param1 = 지하철_노선_요청_생성("신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId(), 10L);
        ExtractableResponse<Response> createResponse1 = 지하철_노선_등록되어_있음(param1, "/lines");
        StationResponse 홍대입구역 = stationService.saveStation(new StationRequest("홍대입구역"));
        StationResponse 신촌역 = stationService.saveStation(new StationRequest("신촌역"));
        LineRequest param2 = 지하철_노선_요청_생성("2호선", "bg-green-600", 홍대입구역.getId(), 신촌역.getId(), 10L);
        ExtractableResponse<Response> createResponse2 = 지하철_노선_등록되어_있음(param2, "/lines");

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청("/lines");

        // then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(createResponse1, createResponse2, response);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        StationResponse 역삼역 = stationService.saveStation(new StationRequest("역삼역"));
        StationResponse 강남역 = stationService.saveStation(new StationRequest("강남역"));
        LineRequest params = 지하철_노선_요청_생성("신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId(), 10L);
        지하철_노선_등록되어_있음(params, "/lines");

        // when
        Long id = 1L;
        ExtractableResponse<Response> response = 지하철_노선_조회_요청("/lines/" + id);

        // then
        지하철_노선_목록_응답됨(response);
    }

    @DisplayName("지하철 노선을 조회할때 종점역(상행, 하행)을 함께 조회한다.")
    @Test
    void getLineWithStations() {
        // given
        StationResponse 강남역 = stationService.saveStation(new StationRequest("강남역"));
        StationResponse 역삼역 = stationService.saveStation(new StationRequest("역삼역"));
        LineRequest params = 지하철_노선_요청_생성("신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId(), 10L);
        지하철_노선_등록되어_있음(params, "/lines");

        // when
        Long id = 1L;
        ExtractableResponse<Response> response = 지하철_노선_조회_요청("/lines/" + id);

        // then
        지하철_노선_목록_응답됨(response);
        assertThat(response.as(LineResponse.class).getStations()).hasSize(2);
        assertThat(response.as(LineResponse.class).getStations().get(0).getId()).isSameAs(강남역.getId());
        assertThat(response.as(LineResponse.class).getStations().get(1).getId()).isSameAs(역삼역.getId());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        StationResponse 강남역 = stationService.saveStation(new StationRequest("강남역"));
        StationResponse 역삼역 = stationService.saveStation(new StationRequest("역삼역"));
        LineRequest createParam = 지하철_노선_요청_생성("신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId(), 10L);
        지하철_노선_등록되어_있음(createParam, "/lines");
        StationResponse 홍대입구역 = stationService.saveStation(new StationRequest("홍대입구역"));
        StationResponse 신촌역 = stationService.saveStation(new StationRequest("신촌역"));
        LineRequest updateParam = 지하철_노선_요청_생성("신분당선", "bg-red-600", 홍대입구역.getId(), 신촌역.getId(), 10L);

        // when
        Long id = 1L;
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(updateParam,"/lines/" + id);

        // then
        지하철_노선_수정됨(강남역.getId(), 역삼역.getId(), updateParam, response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        StationResponse 강남역 = stationService.saveStation(new StationRequest("강남역"));
        StationResponse 역삼역 = stationService.saveStation(new StationRequest("역삼역"));
        LineRequest params = 지하철_노선_요청_생성("신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId(), 10L);
        ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음(params, "/lines");

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(uri);

        // then
        지하철_노선_삭제됨(response);
    }
}
