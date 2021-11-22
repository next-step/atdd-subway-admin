package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = LineApiRequests.지하철_노선_생성_요청(lineRequest);

        // then
        // 지하철_노선_생성됨
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");
        LineApiRequests.지하철_노선_생성_요청(lineRequest);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = LineApiRequests.지하철_노선_생성_요청(lineRequest);

        // then
        // 지하철_노선_생성_실패됨
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        Long id1 = 지하철_노선_등록되어_있음(new LineRequest("2호선", "bg-green-200"));
        Long id2 = 지하철_노선_등록되어_있음(new LineRequest("4호선", "bg-blue-400"));


        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = LineApiRequests.지하철_노선_목록_조회_요청();

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        지하철_노선_응답됨(response);
        지하철_노선_목록_포함됨(response, Arrays.asList(id1, id2));
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest = new LineRequest("2호선", "bg-green-200");
        Long lineId = 지하철_노선_등록되어_있음(lineRequest);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = LineApiRequests.지하철_노선_조회_요청(lineId);

        // then
        // 지하철_노선_응답됨
        지하철_노선_응답(response, lineId, lineRequest);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");
        Long lineId = 지하철_노선_등록되어_있음(lineRequest);
        LineRequest updateRequest = new LineRequest("구분당선", "bg-blue-600");

        // when
        // 지하철_노선_수정_요청
        LineApiRequests.지하철_노선_수정_요청(lineId, updateRequest);

        // then
        // 지하철_노선_수정됨
        지하철_노선_수정됨(lineId, updateRequest);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");
        Long lineId = 지하철_노선_등록되어_있음(lineRequest);

        // when
        // 지하철_노선_제거_요청
        LineApiRequests.지하철_노선_제거_요청(lineId);

        // then
        // 지하철_노선_삭제됨
        지하철_노선_삭제됨(lineId);
    }

    private void 지하철_노선_삭제됨(Long lineId) {
        ExtractableResponse<Response> response = LineApiRequests.지하철_노선_조회_요청(lineId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void 지하철_노선_응답(ExtractableResponse<Response> response, Long lineId, LineRequest lineRequest) {
        LineResponse result = response.jsonPath().getObject(".", LineResponse.class);
        assertThat(result.getId()).isEqualTo(lineId);
        assertThat(result.getName()).isEqualTo(lineRequest.getName());
        assertThat(result.getColor()).isEqualTo(lineRequest.getColor());
    }

    private Long 지하철_노선_등록되어_있음(LineRequest lineRequest) {
        ExtractableResponse<Response> createResponse = LineApiRequests.지하철_노선_생성_요청(lineRequest);
        return Long.valueOf(createResponse.header("Location").split("/")[2]);
    }

    private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response, List<Long> givenLines) {
        List<Long> resultList = response.jsonPath().getList("id", Long.class);
        assertThat(resultList).containsAll(givenLines);
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_노선_수정됨(Long lineId, LineRequest updateRequest) {
        ExtractableResponse<Response> response = LineApiRequests.지하철_노선_조회_요청(lineId);
        LineResponse result = response.jsonPath().getObject(".", LineResponse.class);
        assertThat(result.getId()).isEqualTo(lineId);
        assertThat(result.getName()).isEqualTo(updateRequest.getName());
        assertThat(result.getColor()).isEqualTo(updateRequest.getColor());
    }

}
