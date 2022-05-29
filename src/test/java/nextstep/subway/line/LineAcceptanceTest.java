package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceTestMethods.지하철노선_목록_응답;
import static nextstep.subway.line.LineAcceptanceTestMethods.지하철노선_목록_조회;
import static nextstep.subway.line.LineAcceptanceTestMethods.지하철노선_목록_포함;
import static nextstep.subway.line.LineAcceptanceTestMethods.지하철노선_생성;
import static nextstep.subway.line.LineAcceptanceTestMethods.지하철노선_생성됨;
import static nextstep.subway.line.LineAcceptanceTestMethods.지하철노선_수정;
import static nextstep.subway.line.LineAcceptanceTestMethods.지하철노선_수정됨;
import static nextstep.subway.line.LineAcceptanceTestMethods.지하철노선_응답;
import static nextstep.subway.line.LineAcceptanceTestMethods.지하철노선_제거;
import static nextstep.subway.line.LineAcceptanceTestMethods.지하철노선_제거됨;
import static nextstep.subway.line.LineAcceptanceTestMethods.지하철노선_조회;
import static nextstep.subway.line.LineAcceptanceTestMethods.지하철노선_포함;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.StationAcceptanceTestMethods;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 노선 관련 인수테스트")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final int DISTANCE = 10;
    private StationResponse 판교역;
    private StationResponse 강남역;

    @BeforeEach
    void beforeEach() {
        판교역 = StationAcceptanceTestMethods.지하철역_생성(StationRequest.from("판교역")).as(StationResponse.class);
        강남역 = StationAcceptanceTestMethods.지하철역_생성(StationRequest.from("강남역")).as(StationResponse.class);
    }

    /**
     * When : 지하철 노선을 생성하면
     * Then : 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {

        LineRequest lineRequest = LineRequest.of("신분당선", "RED", 판교역.getId(), 강남역.getId(), DISTANCE);

        // when
        ExtractableResponse<Response> 신분당선_생성_response = 지하철노선_생성(lineRequest);

        // then
        지하철노선_생성됨(신분당선_생성_response);
    }

    /**
     * Given : 2개의 지하철 노선을 생성하고
     * When : 지하철 노선 목록을 조회하면
     * Then : 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        LineRequest 신분당선_request = LineRequest.of("신분당선", "RED", 판교역.getId(), 강남역.getId(), DISTANCE);
        LineRequest 분당선_request = LineRequest.of("분당선", "YELLOW", 판교역.getId(), 강남역.getId(), DISTANCE);

        ExtractableResponse<Response> 신분당선_생성_response = 지하철노선_생성(신분당선_request);
        ExtractableResponse<Response> 분당선_생성_response = 지하철노선_생성(분당선_request);

        // when
        ExtractableResponse<Response> 목록_조회_response = 지하철노선_목록_조회();

        // then
        지하철노선_목록_응답(목록_조회_response);
        지하철노선_목록_포함(목록_조회_response, Arrays.asList(신분당선_생성_response, 분당선_생성_response));
    }

    /**
     * Given : 지하철 노선을 생성하고
     * When : 생성한 지하철 노선을 조회하면
     * Then : 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선을 조회한다.")
    @Test
    void getLine() {
        // given
        LineRequest 신분당선_request = LineRequest.of("신분당선", "RED", 판교역.getId(), 강남역.getId(), DISTANCE);
        ExtractableResponse<Response> 신분당선_생성_response = 지하철노선_생성(신분당선_request);

        // when
        Long lineId = parseIdFromLocationHeader(신분당선_생성_response);
        ExtractableResponse<Response> 지하철노선_조회_response = 지하철노선_조회(lineId);

        // then
        지하철노선_응답(지하철노선_조회_response);
        지하철노선_포함(지하철노선_조회_response, 신분당선_생성_response);
    }

    /**
     * Given : 지하철 노선을 생성하고
     * When : 생성한 지하철 노선을 수정하면
     * Then : 해당 지하철 노선 정보는 수정된다.
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        LineRequest 신분당선_request = LineRequest.of("신분당선", "RED", 판교역.getId(), 강남역.getId(), DISTANCE);
        ExtractableResponse<Response> 신분당선_생성_response = 지하철노선_생성(신분당선_request);

        // when
        Long createdId = parseIdFromLocationHeader(신분당선_생성_response);
        LineRequest 수정_신분당선_request = LineRequest.of("(NEW)신분당선", "(NEW)RED", 판교역.getId(), 강남역.getId(), DISTANCE);

        ExtractableResponse<Response> response = 지하철노선_수정(createdId, 수정_신분당선_request);

        // then
        지하철노선_수정됨(response);
    }

    /**
     * Given : 지하철 노선을 생성하고
     * When : 생성한 지하철 노선을 삭제하면
     * Then : 해당 지하철 노선 정보는 삭제된다.
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        LineRequest 신분당선_request = LineRequest.of("신분당선", "RED", 판교역.getId(), 강남역.getId(), DISTANCE);
        ExtractableResponse<Response> 신분당선_생성_response = 지하철노선_생성(신분당선_request);

        // when
        Long createdId = parseIdFromLocationHeader(신분당선_생성_response);
        ExtractableResponse<Response> response = 지하철노선_제거(createdId);

        // then
        지하철노선_제거됨(response);
    }
}
