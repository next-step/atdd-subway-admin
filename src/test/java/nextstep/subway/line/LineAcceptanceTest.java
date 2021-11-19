package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        final ExtractableResponse<Response> response = 지하철_노선_생성_요청("신분당선", "red");

        // then
        지하철_생성됨(response);
        지하철_노선_필드_검증(response, "신분당선", "red");
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_생성_요청("신분당선", "red");

        // when
        final ExtractableResponse<Response> response = 지하철_노선_생성_요청("신분당선", "red");

        // then
        badRequest(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        지하철_노선_생성_요청("신분당선", "red");
        지하철_노선_생성_요청("1호선", "indigo");

        // when
        final ExtractableResponse<Response> response = 지하철_노선_목록_조회();

        // then
        ok(response);
        지하철_노선_리스트_필드_검증(response, Arrays.asList("신분당선", "1호선"), Arrays.asList("red", "indigo"));
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        final Long id = 지하철_노선_생성_요청_후_아이디_반환("신분당선", "red");

        // when
        final ExtractableResponse<Response> response = 지하철_노선_조회(id);

        // then
        ok(response);
        지하철_노선_필드_검증(response, "신분당선", "red");
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        final Long id = 지하철_노선_생성_요청_후_아이디_반환("신분당선", "red");

        // when
        final ExtractableResponse<Response> response = 지하철_노선_수정_요청(id, "구분당선", "blue");

        // then
        ok(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        final Long id = 지하철_노선_생성_요청_후_아이디_반환("신분당선", "red");

        // when
        final ExtractableResponse<Response> response = 지하철_노선_삭제_요청(id);

        // then
        noContent(response);
    }

    private Long 지하철_노선_생성_요청_후_아이디_반환(final String name, final String color) {
        final ExtractableResponse<Response> response = 지하철_노선_생성_요청(name, color);
        return response.jsonPath().getObject(".", LineResponse.class).getId();
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(final String name, final String color) {
        final HashMap<Object, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured
            .given().log().all()
            .body(params).contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회() {
        return RestAssured
            .given().log().all()
            .when().get("/lines")
            .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_조회(final Long id) {
        return RestAssured
            .given().log().all()
            .when().get("/lines/" + id)
            .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(final Long id, final String name, final String color) {
        final Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put("/lines/" + id)
            .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_삭제_요청(final Long id) {
        return RestAssured
            .given().log().all()
            .when().delete("/lines/" + id)
            .then().log().all().extract();
    }

    private void 지하철_생성됨(final ExtractableResponse<Response> response) {
        created(response);
        final Long id = response.jsonPath().getObject(".", LineResponse.class).getId();
        assertThat(response.header("Location")).isEqualTo("/lines/" + id);
    }

    private void 지하철_노선_필드_검증(final ExtractableResponse<Response> response, final String name, final String color) {
        assertThat(response.jsonPath().getObject(".", LineResponse.class)).isNotNull();
        assertThat(response.jsonPath().getString("name")).isEqualTo(name);
        assertThat(response.jsonPath().getString("color")).isEqualTo(color);
    }

    private void 지하철_노선_리스트_필드_검증(final ExtractableResponse<Response> response, final List<String> names,
        final List<String> colors) {
        assertThat(response.jsonPath().getList(".", LineResponse.class)).hasSize(2);
        assertThat(response.jsonPath().getList("name", String.class)).containsAll(names);
        assertThat(response.jsonPath().getList("color", String.class)).containsAll(colors);
    }

    private void created(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void badRequest(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void ok(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void noContent(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
