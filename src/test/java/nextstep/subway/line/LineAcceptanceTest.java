package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.line.LineScenarioMethod.*;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    private LineRequest 신분당선 = new LineRequest("신분당선", "bg-red-600");
    private LineRequest 구분당선 = new LineRequest("구분당선", "bg-blue-600");
    private LineRequest 수인선 = new LineRequest("수인선", "bg-yellow-600");


    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(신분당선);

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("지하철 노선이 이미 등록되어 있는 경우 지하철 노선 생성에 실패한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        지하철_노선_등록되어_있음(신분당선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(신분당선);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void findLines() {
        // given
        지하철_노선_등록되어_있음(신분당선);
        지하철_노선_등록되어_있음(수인선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청("/lines");

        // then
        지하철_노선_조회_응답됨(response);
        지하철_노선_목록_조회_결과_포함됨(response, 신분당선);
        지하철_노선_목록_조회_결과_포함됨(response, 수인선);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void findLine() {
        // given
        String createdLocationUri = 지하철_노선_등록되어_있음(수인선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createdLocationUri);

        // then
        지하철_노선_조회_응답됨(response);
        지하철_노선_조회_결과_일치됨(response, 수인선);
    }

    @DisplayName("지하철 노선이 등록되지 않은 경우 지하철 노선 조회가 실패한다.")
    @Test
    void findLineValidateNotFound() {
        // given
        String notFoundUri = 지하철_노선_등록되어_있지_않음("lines/1");

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(notFoundUri);

        // then
        지하철_노선_조회_실패됨(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        String createdLocationUri = 지하철_노선_등록되어_있음(신분당선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(createdLocationUri, 구분당선);

        // then
        지하철_노선_수정됨(response, createdLocationUri, 구분당선);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        String createdLocationUri = 지하철_노선_등록되어_있음(수인선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(createdLocationUri);

        // then
        지하철_노선_삭제됨(response);
    }

    @DisplayName("지하철 노선이 등록되지 않은 경우 지하철 노선 제거에 실패한다.")
    @Test
    void deleteLineValidateEmptyResult() {
        // given
        String notFoundUri = 지하철_노선_등록되어_있지_않음("lines/1");

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(notFoundUri);

        // then
        지하철_노선_삭제_실패됨(response);
    }
}
