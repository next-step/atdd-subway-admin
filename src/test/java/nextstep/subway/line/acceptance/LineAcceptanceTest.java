package nextstep.subway.line.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
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
import static nextstep.subway.station.StationSteps.*;
import static org.assertj.core.api.Assertions.*;

@DisplayName("지하철 노선 인수 테스트")
public class LineAcceptanceTest extends AcceptanceTest {

    private StationResponse 강남역;
    private StationResponse 역삼역;
    private StationResponse 사당역;
    private StationResponse 방배역;
    private LineResponse 이호선;
    private LineResponse 사호선;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        강남역 = 지하철_역_등록되어_있음(new StationRequest("강남역"));
        역삼역 = 지하철_역_등록되어_있음(new StationRequest("역삼역"));
        사당역 = 지하철_역_등록되어_있음(new StationRequest("사당역"));
        방배역 = 지하철_역_등록되어_있음(new StationRequest("방배역"));
        이호선 = 지하철_노선_등록되어_있음(new LineRequest("2호선", "green", 강남역.getId(), 역삼역.getId(), 10));
        사호선 = 지하철_노선_등록되어_있음(new LineRequest("4호선", "blue", 사당역.getId(), 방배역.getId(), 20));
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        LineRequest lineRequest = new LineRequest("3호선", "orange", 강남역.getId(), 사당역.getId(), 10);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineRequest);

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        LineRequest lineRequest = new LineRequest(이호선.getName(), "red", 강남역.getId(), 사당역.getId(), 10);

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
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(이호선.getId());

        // then
        지하철_노선_응답됨(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        LineRequest updateLineRequest = new LineRequest(이호선.getName(), "red", 역삼역.getId(), 방배역.getId(), 15);

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(이호선.getId(), updateLineRequest);

        // then
        지하철_노선_수정됨(response);
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_삭제_요청(이호선.getId());

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
        List<Long> expectedLineIds = lineResponses.stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(lineIds).containsAll(expectedLineIds);
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
