package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.utils.RequestUtil;
import nextstep.subway.utils.StatusCodeCheckUtil;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        final ExtractableResponse<Response> response = 지하철_노선_생성_요청("신분당선", "red");

        // then
        지하철_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_생성_요청("신분당선", "red");

        // when
        final ExtractableResponse<Response> response = 지하철_노선_생성_요청("신분당선", "red");

        // then
        StatusCodeCheckUtil.badRequest(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        final ExtractableResponse<Response> createResponse1 = 지하철_노선_생성_요청("신분당선", "red");
        final ExtractableResponse<Response> createResponse2 = 지하철_노선_생성_요청("1호선", "indigo");

        // when
        final ExtractableResponse<Response> response = 지하철_노선_목록_조회();

        // then
        StatusCodeCheckUtil.ok(response);
        지하철_노선_리스트_필드_검증(createResponse1, createResponse2, response);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        final ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청("신분당선", "red");

        // when
        final ExtractableResponse<Response> response = 지하철_노선_조회(createResponse);

        // then
        StatusCodeCheckUtil.ok(response);
        지하철_노선_필드_검증(createResponse, response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        final ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청("신분당선", "red");

        // when
        final ExtractableResponse<Response> response = 지하철_노선_수정_요청(createResponse, "구분당선", "blue");

        // then
        StatusCodeCheckUtil.ok(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        final ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청("신분당선", "red");

        // when
        final ExtractableResponse<Response> response = 지하철_노선_삭제_요청(createResponse);

        // then
        StatusCodeCheckUtil.noContent(response);
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(final String name, final String color) {
        final Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RequestUtil.post("/lines", params);
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회() {
        return RequestUtil.get("/lines");
    }

    private ExtractableResponse<Response> 지하철_노선_조회(final ExtractableResponse<Response> createResponse) {
        final String uri = createResponse.header("Location");
        return RequestUtil.get(uri);
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(
        final ExtractableResponse<Response> createResponse, final String name, final String color
    ) {
        final String uri = createResponse.header("Location");
        final Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RequestUtil.put(uri, params);
    }

    private ExtractableResponse<Response> 지하철_노선_삭제_요청(final ExtractableResponse<Response> createResponse) {
        final String uri = createResponse.header("Location");
        return RequestUtil.delete(uri);
    }

    private void 지하철_생성됨(final ExtractableResponse<Response> response) {
        StatusCodeCheckUtil.created(response);
        final Long id = response.jsonPath().getObject(".", LineResponse.class).getId();
        assertThat(response.header("Location")).isEqualTo("/lines/" + id);
    }

    private void 지하철_노선_필드_검증(
        final ExtractableResponse<Response> createResponse, final ExtractableResponse<Response> response
    ) {
        final LineResponse expectedLine = createResponse.jsonPath().getObject(".", LineResponse.class);
        final LineResponse resultLine = response.jsonPath().getObject(".", LineResponse.class);
        assertThat(resultLine).isEqualTo(expectedLine);
    }

    private void 지하철_노선_리스트_필드_검증(
        final ExtractableResponse<Response> createResponse1,
        final ExtractableResponse<Response> createResponse2,
        final ExtractableResponse<Response> response
    ) {
        final List<LineResponse> expectedLines = Stream.of(createResponse1, createResponse2)
            .map(it -> it.jsonPath().getObject(".", LineResponse.class))
            .collect(Collectors.toList());
        final List<LineResponse> resultLines = response.jsonPath().getList(".", LineResponse.class);

        assertThat(resultLines).containsAll(expectedLines);
    }
}
