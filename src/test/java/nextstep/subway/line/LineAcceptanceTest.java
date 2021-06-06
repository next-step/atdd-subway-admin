package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private static final LineRequest 신분당_라인 = new LineRequest("신분당선", "bg-red-600");
    private static final LineRequest lineTwo = new LineRequest("2호선", "bg-green-600");

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        final ExtractableResponse<Response> response = 지하철_노선_생성(신분당_라인);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_생성(신분당_라인);

        // when
        final ExtractableResponse<Response> response = 지하철_노선_생성(신분당_라인);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        final ExtractableResponse<Response> createResponse1 = 지하철_노선_생성(신분당_라인);
        final ExtractableResponse<Response> createResponse2 = 지하철_노선_생성(lineTwo);

        // when
        final ExtractableResponse<Response> response = 지하철_노선_목록_조회();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        final List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(toList());
        final List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        final ExtractableResponse<Response> createResponse = 지하철_노선_생성(신분당_라인);

        // when
        final String uri = createResponse.header("Location");
        final ExtractableResponse<Response> response = 지하철_노선_조회(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        final ExtractableResponse<Response> createResponse = 지하철_노선_생성(신분당_라인);

        // when
        final String uri = createResponse.header("Location");
        final Map<String, String> params = createParams("구분당선", "bg-blue-600");
        final ExtractableResponse<Response> response = 지하철_노선_수정(uri, params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        final ExtractableResponse<Response> createResponse = 지하철_노선_생성(신분당_라인);

        // when
        final String uri = createResponse.header("Location");
        final ExtractableResponse<Response> response = 지하철_노선_제거(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 지하철_노선_생성(final LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_조회(final String uri) {
        return RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정(final String uri, final Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_제거(final String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }

    private Map<String, String> createParams(final String name, final String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return params;
    }
}
