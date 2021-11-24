package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void 지하철_노선을_생성한다() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = requestLineCreation(params);

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void 기존에_존재하는_지하철_노선_이름으로_지하철_노선을_생성한다() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        ExtractableResponse<Response> response = requestLineCreation(params);

        // when
        // 지하철_노선_중복_생성_요청
        ExtractableResponse<Response> response2 = requestLineCreation(params);

        assertThat(response2.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void 지하철_노선_목록을_조회한다() {
        /// given
        // 지하철_노선_등록되어_있음
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        ExtractableResponse<Response> createResponse1 = requestLineCreation(params1);

        // 지하철_노선_등록되어_있음
        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "grey darken-2");
        params2.put("name", "9호선");
        ExtractableResponse<Response> createResponse2 = requestLineCreation(params2);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = requestReadLines("/lines");

        // then
        // 지하철_노선_등록되어_있음
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = getExpectedLineIds(createResponse1, createResponse2);
        List<Long> resultLineIds = getLineIds(response);
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void 지하철_노선을_조회한다() {
        /// given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = new HashMap<>();
        params.put("name", "9호선");
        ExtractableResponse<Response> createResponse = requestLineCreation(params);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = requestReadLines(uri);

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Long expectedLineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        Long resultLineId = response.jsonPath().getLong("id");
        assertThat(resultLineId).isEqualTo(expectedLineId);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void 지하철_노선을_수정한다() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "9호선");
        ExtractableResponse<Response> createResponse = requestLineCreation(params1);
        String uri = createResponse.header("Location");

        // when
        // 지하철_노선_수정
        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "1호선");
        ExtractableResponse<Response> updateResponse = requestLineUpdate(uri, params2);

        // 재조회
        ExtractableResponse<Response> response = requestReadLines(uri);
        // then
        // 지하철_노선_수정됨
        assertThat(createResponse.jsonPath().getLong("id")).isEqualTo(response.jsonPath().getLong("id"));
        assertThat(response.jsonPath().getString("name")).isEqualTo("1호선");
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        /// given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = new HashMap<>();
        params.put("name", "9호선");
        ExtractableResponse<Response> createResponse = requestLineCreation(params);

        // when
        // 지하철_노선_제거_요청
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = requestLineDelete(uri);

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> requestLineDelete(String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> requestLineCreation(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private List<Long> getLineIds(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
    }

    private List<Long> getExpectedLineIds(ExtractableResponse<Response> createResponse1, ExtractableResponse<Response> createResponse2) {
        return Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
    }

    private ExtractableResponse<Response> requestReadLines(String uri) {
        return RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> requestLineUpdate(String uri, Map<String, String> params2) {
        return RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();
    }
}
