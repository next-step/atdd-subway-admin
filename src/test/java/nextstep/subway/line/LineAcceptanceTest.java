package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "red");

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_등록되어_있음("신분당선", "red");
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "red");

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        long id1 = 지하철_노선_등록되어_있음("신분당선", "red");
        long id2 = 지하철_노선_등록되어_있음("2호선", "green");
        LocalDateTime now = LocalDateTime.now();
        List<LineResponse> expected = Arrays.asList(new LineResponse(id1, "신분당선", "red", now, now),
            new LineResponse(id2, "2호선", "green", now, now));

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(response, expected);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        long id = 지하철_노선_등록되어_있음("신분당선", "red");
        LocalDateTime now = LocalDateTime.now();
        LineResponse expected = new LineResponse(id, "신분당선", "red", now, now);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(id);

        // then
        지하철_노선_응답됨(response, expected);
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회하여 실패한다.")
    @Test
    void getLine_throwsExceptionWHenNoExist() {
        // given

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(1L);

        // then
        지하철_노선_미존재_응답됨(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        long id = 지하철_노선_등록되어_있음("신분당선", "red");
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "green");
        LocalDateTime now = LocalDateTime.now();
        LineResponse expected = new LineResponse(id, "2호선", "green", now, now);

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(id, params);

        // then
        지하철_노선_수정됨(response, expected);
    }

    @DisplayName("존재하지 않는 지하철 노선을 수정하여 실패한다.")
    @Test
    void updateLine_throwsExceptionWHenNoExist() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "green");

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(1L, params);

        // then
        지하철_노선_미존재_응답됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        long id = 지하철_노선_등록되어_있음("신분당선", "red");

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(id);

        // then
        지하철_노선_삭제됨(response);
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, String> params) {
        // when
        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all().extract();
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private long 지하철_노선_등록되어_있음(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);
        return Long.parseLong(response.header("Location").split("/")[2]);
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        // when
        return RestAssured
            .given().log().all()
            .when().get("/lines")
            .then().log().all().extract();
    }

    private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response, List<LineResponse> expected) {
        Set<LineResponse> lineResponses = new HashSet<>(response.jsonPath().getList(".", LineResponse.class));

        for (LineResponse lineResponse : expected) {
            assertThat(lineResponses.contains(lineResponse)).isTrue();
        }
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(long id) {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/lines/" + id)
            .then().log().all().extract();
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response, LineResponse expected) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.as(LineResponse.class)).isEqualTo(expected);
    }

    private void 지하철_노선_미존재_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(long id, Map<String, String> params) {
        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put("/lines/" + id)
            .then().log().all().extract();
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response, LineResponse expected) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> getResponse = 지하철_노선_조회_요청(expected.getId());
        지하철_노선_응답됨(getResponse, expected);
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(long id) {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete("/lines/" + id)
            .then().log().all().extract();
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
