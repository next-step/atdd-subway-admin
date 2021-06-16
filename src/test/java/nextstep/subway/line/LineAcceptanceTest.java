package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.line.LineStepTest.*;
import static nextstep.subway.station.StationAcceptanceTest.TEST_GANGNAM_STATION;
import static nextstep.subway.station.StationAcceptanceTest.TEST_YUCKSAM_STATION;
import static nextstep.subway.station.StationStepTest.지하철_역_등록되어_있음;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    public static final LineRequest TEST_FIRST_LINE = new LineRequest("1호선", "red");
    public static final LineRequest TEST_SECOND_LINE = new LineRequest("2호선", "blue");

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(TEST_FIRST_LINE);

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("두 종점역은 구간의 형태로 관리되는 지하철 노선을 생성한다.")
    @Test
    void createLineWithSection() {
        //given
        long kangnamId = 지하철_역_등록되어_있음(TEST_GANGNAM_STATION);
        long yucksamId = 지하철_역_등록되어_있음(TEST_YUCKSAM_STATION);

        LineRequest firstLine = new LineRequest("1호선", "red", kangnamId, yucksamId, 10L);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(firstLine);

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        지하철_노선_등록되어_있음(TEST_FIRST_LINE);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(TEST_FIRST_LINE);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        long firstLineId = 지하철_노선_등록되어_있음(TEST_FIRST_LINE);
        long secondLineId = 지하철_노선_등록되어_있음(TEST_SECOND_LINE);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        //then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(response, firstLineId, secondLineId);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        long createdId = 지하철_노선_등록되어_있음(TEST_FIRST_LINE);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createdId);

        // then
        지하철_노선_목록_응답됨(response);
        지하철_노선_같음(createdId, response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        long createdId = 지하철_노선_등록되어_있음(TEST_FIRST_LINE);
        LineRequest parameter = new LineRequest("1호선", "black");

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(createdId, parameter);

        // then
        지하철_노선_수정됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        long firstLine = 지하철_노선_등록되어_있음(TEST_FIRST_LINE);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(firstLine);

        // then
        지하철_노선_삭제됨(response);
    }
}
