package nextstep.subway.line.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.acceptance.StationAcceptance;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        Long upStationId = StationAcceptance.getStationId(StationAcceptance.create_station("강남역"));
        Long downStationId = StationAcceptance.getStationId(StationAcceptance.create_station("양재역"));
        LineAcceptance.getLineId(
                LineAcceptance.create_line("신분당선", "bg-red-600", upStationId, downStationId, 10));

        // then
        List<String> lineNames = LineAcceptance.line_list_was_queried();
        assertThat(lineNames).containsAnyOf("신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        Long upStationId = StationAcceptance.getStationId(StationAcceptance.create_station("강남역"));
        Long downStationId = StationAcceptance.getStationId(StationAcceptance.create_station("양재역"));
        LineAcceptance.getLineId(
                LineAcceptance.create_line("신분당선", "bg-red-600", upStationId, downStationId, 10));
        Long downStationId2 = StationAcceptance.getStationId(StationAcceptance.create_station("역삼역"));
        LineAcceptance.create_line("2호선", "bg-green-600", upStationId, downStationId2, 10);

        // when
        List<String> stationNames = LineAcceptance.line_list_was_queried();

        // then
        assertThat(stationNames).containsAll(Arrays.asList("신분당선", "2호선"));
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        Long upStationId = StationAcceptance.getStationId(StationAcceptance.create_station("강남역"));
        Long downStationId = StationAcceptance.getStationId(StationAcceptance.create_station("양재역"));
        Long lineId = LineAcceptance.getLineId(
                LineAcceptance.create_line("신분당선", "bg-red-600", upStationId, downStationId, 10));

        // when
        String name = LineAcceptance.line_was_queried(lineId);

        // then
        assertEquals("신분당선", name);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        Long upStationId = StationAcceptance.getStationId(StationAcceptance.create_station("강남역"));
        Long downStationId = StationAcceptance.getStationId(StationAcceptance.create_station("양재역"));
        Long lineId = LineAcceptance.getLineId(
                LineAcceptance.create_line("신분당선", "bg-red-600", upStationId, downStationId, 10));

        // when
        LineAcceptance.update_line(lineId, "다른분당선", "bg-red-600");

        // then
        String name = LineAcceptance.line_was_queried(lineId);
        assertEquals("다른분당선", name);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        Long upStationId = StationAcceptance.getStationId(StationAcceptance.create_station("강남역"));
        Long downStationId = StationAcceptance.getStationId(StationAcceptance.create_station("양재역"));
        Long lineId = LineAcceptance.getLineId(
                LineAcceptance.create_line("신분당선", "bg-red-600", upStationId, downStationId, 10));

        // when
        ExtractableResponse<Response> response = LineAcceptance.delete_line(lineId);

        // then
        assertEquals(HttpStatus.NO_CONTENT.value(), response.statusCode());
    }
}
