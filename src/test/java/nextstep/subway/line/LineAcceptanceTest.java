package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceTool.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response
            = 지하철_노선_생성_요청(신분당선);

        // then
        // 지하철_노선_생성됨
        지하철_노선_생성됨(신분당선, response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음(신분당선);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response
            = 지하철_노선_생성_요청(신분당선);

        // then
        // 지하철_노선_생성_실패됨
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse1
            = 지하철_노선_등록되어_있음(신분당선);
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse2
            = 지하철_노선_등록되어_있음(분당선);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response
            = 지하철_노선_목록_조회_요청();

        // then
        // 지하철_노선_목록_응답됨
        지하철_노선_목록_응답됨(response);
        // 지하철_노선_목록_포함됨
        지하철_노선_목록_포함됨(response, createResponse1, createResponse2);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse
            = 지하철_노선_등록되어_있음(신분당선);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response
            = 지하철_노선_조회_요청(createResponse);

        // then
        // 지하철_노선_응답됨
        지하철_노선_응답됨(response, 신분당선);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse
            = 지하철_노선_등록되어_있음(신분당선);

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> updateResponse
            = 지하철_노선_수정_요청(createResponse, 분당선);

        ExtractableResponse<Response> changedResponse
            = 지하철_노선_조회_요청(createResponse);

        // then
        // 지하철_노선_수정됨
        지하철_노선_수정됨(updateResponse, changedResponse, 분당선);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse
            = 지하철_노선_등록되어_있음(신분당선);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response
            = 지하철_노선_제거_요청(createResponse);

        ExtractableResponse<Response> notFoundResponse
            = 지하철_노선_조회_요청(createResponse);

        // then
        // 지하철_노선_삭제됨
        지하철_노선_삭제됨(response, notFoundResponse);
    }
}
