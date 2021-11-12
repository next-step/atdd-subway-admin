package nextstep.subway.line.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.line.LineSteps.*;
import static org.assertj.core.api.Assertions.*;

@DisplayName("지하철 노선 인수 테스트")
public class LineAcceptanceTest extends AcceptanceTest {

    private LineResponse 이호선;
    private LineResponse 사호선;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        이호선 = 지하철_노선_등록되어_있음(new LineRequest("2호선", "green", 1L, 2L, 10));
        사호선 = 지하철_노선_등록되어_있음(new LineRequest("4호선", "blue", 3L, 4L, 20));
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        LineRequest lineRequest = new LineRequest("3호선", "orange", 1L, 2L, 10);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineRequest);

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        LineRequest lineRequest = new LineRequest(이호선.getName(), "red", 1L, 2L, 10);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineRequest);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(response, Arrays.asList(이호선, 사호선));
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(이호선);

        // then
        지하철_노선_응답됨(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        LineRequest updateLineRequest = new LineRequest(이호선.getName(), "red", 5L, 6L, 15);

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(이호선.getId(), updateLineRequest);

        // then
        지하철_노선_수정됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(이호선.getId());

        // then
        지하철_노선_삭제됨(response);
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response, List<LineResponse> lineResponses) {
        List<Long> lineIds = new ArrayList<>(response.jsonPath().getList(".", LineResponse.class))
                .stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        List<Long> expectedPlaceIds = lineResponses.stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(lineIds).containsAll(expectedPlaceIds);
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
