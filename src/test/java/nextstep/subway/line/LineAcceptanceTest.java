package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.line.LineAcceptanceMethod.*;
import static nextstep.subway.station.StationAcceptanceMethod.*;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends BaseAcceptanceTest {
    private final String ID_KEY = "id";
    private final String LINE_COLOR_RED = "bg-red-600";
    private final String LINE_COLOR_GREEN = "bg-green-600";
    private final int DISTANCE = 10;

    private Long 지하철역_ID;
    private Long 새로운지하철역_ID;
    private Long 또다른지하철역_ID;

    @BeforeEach
    void createStations() {
        this.지하철역_ID = 지하철역_생성("지하철역").jsonPath().getLong(ID_KEY);
        this.새로운지하철역_ID = 지하철역_생성("새로운지하철역").jsonPath().getLong(ID_KEY);
        this.또다른지하철역_ID = 지하철역_생성("또다른지하철역").jsonPath().getLong(ID_KEY);
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        String 신분당선 = "신분당선";
        LineRequest 신분당선_생성_요청 = LineRequest.of(신분당선, LINE_COLOR_RED, 지하철역_ID, 새로운지하철역_ID, DISTANCE);
        지하철노선_생성(신분당선_생성_요청);

        // then
        생성한_지하철노선_찾기(신분당선);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        String 신분당선 = "신분당선";
        LineRequest 신분당선_생성_요청 = LineRequest.of(신분당선, LINE_COLOR_RED, 지하철역_ID, 새로운지하철역_ID, DISTANCE);
        지하철노선_생성(신분당선_생성_요청);
        String 분당선 = "분당선";
        LineRequest 분당선_생성_요청 = LineRequest.of(분당선, LINE_COLOR_GREEN, 지하철역_ID, 또다른지하철역_ID, DISTANCE);
        지하철노선_생성(분당선_생성_요청);

        // when & then
        생성한_지하철노선_찾기(신분당선, 분당선);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답 받을 수 있다
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        String 신분당선 = "신분당선";
        LineRequest 신분당선_생성_요청 = LineRequest.of(신분당선, LINE_COLOR_RED, 지하철역_ID, 새로운지하철역_ID, DISTANCE);
        ExtractableResponse<Response> 신분당선_생성_응답 = 지하철노선_생성(신분당선_생성_요청);

        // when
        ExtractableResponse<Response> 신분당선_조회_응답 = 지하철노선_ID_조회(신분당선_생성_응답);

        // then
        지하철노선_조회_응답_확인(신분당선_조회_응답, 신분당선);
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
        // when
        // then
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
        // when
        // then
    }
}
