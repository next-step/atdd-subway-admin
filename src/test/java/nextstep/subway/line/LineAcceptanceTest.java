항package nextstep.subway.line;

import static nextstep.subway.station.StationAcceptanceTest.지하철_역_생성;
import static nextstep.subway.utils.AcceptanceTestUtil.delete;
import static nextstep.subway.utils.AcceptanceTestUtil.get;
import static nextstep.subway.utils.AcceptanceTestUtil.post;
import static nextstep.subway.utils.AcceptanceTestUtil.put;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    private void setup() {
        지하철_역_생성(new StationRequest("강남역"));
        지하철_역_생성(new StationRequest("청계산입구역"));
        지하철_역_생성(new StationRequest("역삼역"));
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response =
            지하철_노선_생성(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10));

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_생성(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10));

        // when
        final ExtractableResponse<Response> response =
            지하철_노선_생성(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10));

        // then
        존재하는_지하철_노선_이름인_경우_노선_생성_실패(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> createResponse1 =
            지하철_노선_생성(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 5));
        ExtractableResponse<Response> createResponse2 =
            지하철_노선_생성(new LineRequest("2호선", "bg-green-600", 1L, 3L, 10));

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회();

        // then
        노선_목록_두개가_응답에_포함(createResponse1, createResponse2, response);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse =
            지하철_노선_생성(new LineRequest("2호선", "bg-green-600", 1L, 3L, 10));

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회(createResponse.header("Location"));

        // then
        노선_2호선_green_1개가_응답에_포함(response);
    }

    @Test
    @DisplayName("없는 id로 조회시 400응답")
    void getLineWhenNotExistEntity() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_조회("/lines/1");

        // then
        잘못된_요청_응답(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse =
            지하철_노선_생성(new LineRequest("2호선", "bg-green-600", 1L, 3L, 10));

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정(createResponse.header("Location"),
            new LineRequest("신분당선", "bg-red-600"));

        // then
        변경된_노선_신분당선_red가_응답에_포함(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_생성(
            new LineRequest("2호선", "bg-green-600", 1L, 3L, 10));

        // when
        ExtractableResponse<Response> response = 지하철_노선_삭제(createResponse.header("Location"));

        // then
        노선_삭제됨(response);
    }

    public static ExtractableResponse<Response> 지하철_노선_생성(LineRequest lineRequest) {
        return post("/lines", lineRequest);
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회() {
        return get("/lines");
    }

    private ExtractableResponse<Response> 지하철_노선_조회(String uri) {
        return get(uri);
    }

    private ExtractableResponse<Response> 지하철_노선_수정(String uri, LineRequest request) {
        return put(uri, request);
    }

    private ExtractableResponse<Response> 지하철_노선_삭제(String uri) {
        return delete(uri);
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private void 존재하는_지하철_노선_이름인_경우_노선_생성_실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private void 잘못된_요청_응답(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("해당하는 Line이 없습니다. id = 1");
    }

    private void 노선_목록_두개가_응답에_포함(ExtractableResponse<Response> createResponse1,
        ExtractableResponse<Response> createResponse2,
        ExtractableResponse<Response> response) {

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(it -> it.getId())
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    private void 노선_2호선_green_1개가_응답에_포함(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getObject(".", LineResponse.class))
            .extracting("name", "color")
            .contains("2호선", "bg-green-600");
    }

    private void 변경된_노선_신분당선_red가_응답에_포함(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getObject(".", LineResponse.class))
            .extracting("name", "color")
            .contains("신분당선", "bg-red-600");
    }

    private void 노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
