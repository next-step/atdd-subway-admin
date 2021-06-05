package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.line.LineSteps.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private LineRequest 분당선;
    private LineRequest 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();
        분당선 = new LineRequest("분당선", "yellow", 1L, 2L, 10);
        신분당선 = new LineRequest("신분당선", "red", 1L, 2L, 20);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(분당선);

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_생성_요청(분당선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(분당선);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> 생성된_분당선 = 지하철_노선_생성_요청(분당선);
        ExtractableResponse<Response> 생성된_신분당선 = 지하철_노선_생성_요청(신분당선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(response, Arrays.asList(생성된_분당선, 생성된_신분당선));
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> 생성된_분당선 = 지하철_노선_생성_요청(분당선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(생성된_분당선);

        // then
        지하철_노선_응답됨(response, 생성된_분당선);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> 생성된_분당선 = 지하철_노선_생성_요청(분당선);
        LineRequest 수정내용 = 신분당선;

        // when
        ExtractableResponse<Response> 수정된_분당선 = 지하철_노선_수정_요청(수정내용, 생성된_분당선);

        // then
        지하철_노선_수정됨(수정된_분당선);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> 생성된_분당선 = 지하철_노선_생성_요청(분당선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(생성된_분당선);

        // then
        지하철_노선_삭제됨(response);
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response,
                               List<ExtractableResponse<Response>> createdLineResponses) {
        List<Long> expectedLineIds = createdLineResponses.stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());

        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response,
                            ExtractableResponse<Response> createdResponse) {
        LineResponse line = createdResponse.jsonPath().getObject(".", LineResponse.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath()
                        .getObject(".", LineResponse.class)
                        .getName()).isEqualTo(line.getName())
        );
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
