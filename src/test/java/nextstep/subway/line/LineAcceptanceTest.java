package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.utils.RequestTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = createLineRequest(new LineRequest("2호선", "초록"));

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest = new LineRequest("2호선", "초록");
        createLineRequest(lineRequest);
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = createLineRequest(lineRequest);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse createResponse = createLineRequest(new LineRequest("2호선", "초록"))
                .as(LineResponse.class);
        // 지하철_노선_등록되어_있음
        LineResponse createResponse2 = createLineRequest(new LineRequest("1호선", "파랑"))
                .as(LineResponse.class);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = selectLines();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        List<LineResponse> result = response.jsonPath()
                .getList(".", LineResponse.class);
        assertThat(result).contains(createResponse, createResponse2);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse createdLineResponse = createLineRequest(new LineRequest("2호선", "초록"))
                .as(LineResponse.class);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = selectLineWithId(createdLineResponse.getId());

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_정보_확인
        LineResponse selectedLineResponse = response.as(LineResponse.class);
        assertThat(selectedLineResponse).isEqualTo(createdLineResponse);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse lineResponse = createLineRequest(new LineRequest("2호선", "초록"))
                .as(LineResponse.class);

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = updateLineRequest(lineResponse.getId(), new LineRequest("2호선", "빨강"));

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse updatedResponse = response.as(LineResponse.class);
        assertThat(updatedResponse.getName()).isEqualTo("2호선");
        assertThat(updatedResponse.getColor()).isEqualTo("빨강");
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse lineResponse = createLineRequest(new LineRequest("2호선", "초록"))
                .as(LineResponse.class);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = deleteLineRequest(lineResponse.getId());

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> createLineRequest(LineRequest lineRequest) {
        final String url = "/lines";
        return RequestTest.doPost(url, lineRequest);
    }

    private ExtractableResponse<Response> selectLines() {
        final String url = "/lines";
        return RequestTest.doGet(url);
    }

    private ExtractableResponse<Response> selectLineWithId(Long lineId) {
        final String url = "/lines/" + lineId;
        return RequestTest.doGet(url);
    }

    private ExtractableResponse<Response> updateLineRequest(Long lineId, LineRequest lineRequest) {
        final String url = "/lines/" + lineId;
        return RequestTest.doPut(url, lineRequest);
    }

    private ExtractableResponse<Response> deleteLineRequest(Long lineId) {
        final String url = "/lines/" + lineId;
        return RequestTest.doDelete(url);
    }
}
