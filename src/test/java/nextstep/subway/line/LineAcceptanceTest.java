package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceMethods.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private static final String LINE_URL_PATH = "/lines";

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        LineRequest lineRequest = LineRequest.from("신분당선", "RED");

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(LINE_URL_PATH, lineRequest);

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        LineRequest lineRequest = LineRequest.from("신분당선", "RED");
        지하철_노선_등록되어_있음(LINE_URL_PATH, lineRequest);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(LINE_URL_PATH, lineRequest);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        LineRequest firstLineRequest = LineRequest.from("신분당선", "RED");
        LineRequest secondLineRequest = LineRequest.from("1호선", "BLUE");

        ExtractableResponse<Response> firstCreateResponse = 지하철_노선_등록되어_있음(LINE_URL_PATH, firstLineRequest);
        ExtractableResponse<Response> secondCreateResponse = 지하철_노선_등록되어_있음(LINE_URL_PATH, secondLineRequest);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청(LINE_URL_PATH);

        // then
        List<Long> createLineIds = Stream.of(firstCreateResponse, secondCreateResponse)
                                         .map(this::parseIdFromLocationHeader)
                                         .collect(Collectors.toList());
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(response, createLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        LineRequest lineRequest = LineRequest.from("신분당선","RED");
        ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음(LINE_URL_PATH, lineRequest);

        // when
        String location = createResponse.header(LOCATION_HEADER_NAME);
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(location);

        // then
        지하철_노선_응답됨(response);
        지하철_노선_포함됨(response, parseIdFromLocationHeader(createResponse));
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        LineRequest lineRequest = LineRequest.from("신분당선","RED");
        ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음(LINE_URL_PATH, lineRequest);

        // when
        String location = createResponse.header(LOCATION_HEADER_NAME);
        LineRequest updateLineRequest = LineRequest.from("신분당선(수정)","RED(수정)");
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(location, updateLineRequest);

        // then
        지하철_노선_수정됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        LineRequest lineRequest = LineRequest.from("신분당선","RED");
        ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음(LINE_URL_PATH, lineRequest);

        // when
        String location = createResponse.header(LOCATION_HEADER_NAME);
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(location);

        // then
        지하철_노선_삭제됨(response);
    }
}
