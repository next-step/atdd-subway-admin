package nextstep.subway.line;

import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    Map<String, String> 신분당선 = new HashMap<String, String>() {{
        put("name", "신분당선");
        put("color", "red darken-1");
    }};
    Map<String, String> 분당선 = new HashMap<String, String>() {{
        put("name", "분당선");
        put("color", "yellow darken-1");
    }};

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response
            = 지하철_노선_생성_요청(신분당선);

        // then
        // 지하철_노선_생성됨
        지하철_노선_생성됨(신분당선, response);
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, String> params) {
        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all()
            .extract();
    }

    private void 지하철_노선_생성됨(Map<String, String> params, ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.header("Location")).isNotBlank();
        assertThat(response.jsonPath().getLong("id")).isNotNull();
        assertThat(response.jsonPath().getString("name")).isEqualTo(params.get("name"));
        assertThat(response.jsonPath().getString("color")).isEqualTo(params.get("color"));
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음(신분당선);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response
            = 지하철_노선_생성_요청(신분당선);

        // then
        // 지하철_노선_생성_실패됨
        지하철_노선_생성_실패됨(response);
    }

    private ExtractableResponse<Response> 지하철_노선_등록되어_있음(Map<String, String> 지하철_노선_정보_신분당선) {
        Map<String, String> params1 = 지하철_노선_정보_신분당선;
        return 지하철_노선_생성_요청(params1);
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse1
            = 지하철_노선_등록되어_있음(신분당선);
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse2
            = 지하철_노선_등록되어_있음(분당선);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response
            = 지하철_노선_목록_조회_요청();

        // then
        // 지하철_노선_목록_응답됨
        지하철_노선_목록_응답됨(response);
        // 지하철_노선_목록_포함됨
        지하철_노선_목록_포함됨(response, createResponse1, createResponse2);
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
            .given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/lines")
            .then().log().all().extract();
    }

    private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
    }

    private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response,
                ExtractableResponse<Response>... createResponses) {
        List<Long> expectedLineIds = Arrays.stream(createResponses)
            .map(res -> res.jsonPath().getLong("id"))
            .collect(toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(res -> res.getId())
            .collect(toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse
            = 지하철_노선_등록되어_있음(신분당선);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response
            = 지하철_노선_조회_요청(createResponse);

        // then
        // 지하철_노선_응답됨
        지하철_노선_응답됨(response, 신분당선);
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(ExtractableResponse<Response> createResponse) {
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/lines/{id}", createResponse.jsonPath().getLong("id"))
            .then().log().all().extract();
        return response;
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response, Map<String, String> params) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.jsonPath().getLong("id")).isNotNull();
        assertThat(response.jsonPath().getString("name")).isEqualTo(params.get("name"));
        assertThat(response.jsonPath().getString("color")).isEqualTo(params.get("color"));
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse
            = 지하철_노선_등록되어_있음(신분당선);

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> updateResponse
            = 지하철_노선_수정_요청(createResponse, 분당선);

        ExtractableResponse<Response> changedResponse
            = 지하철_노선_조회_요청(createResponse);

        // then
        // 지하철_노선_수정됨
        지하철_노선_수정됨(updateResponse, changedResponse, 분당선);
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(
        ExtractableResponse<Response> createResponse, Map<String, String> params) {
        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put("/lines/{id}", createResponse.jsonPath().getLong("id"))
            .then().log().all().extract();
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> updateResponse,
            ExtractableResponse<Response> changedResponse, Map<String, String> params) {
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        지하철_노선_응답됨(changedResponse, params);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse
            = 지하철_노선_등록되어_있음(신분당선);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response
            = 지하철_노선_제거_요청(createResponse);

        ExtractableResponse<Response> notFoundResponse
            = 지하철_노선_조회_요청(createResponse);

        // then
        // 지하철_노선_삭제됨
        지하철_노선_삭제됨(response, notFoundResponse);
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(ExtractableResponse<Response> createResponse) {
        return RestAssured
            .given().log().all()
            .when().delete("/lines/{id}", createResponse.jsonPath().getLong("id"))
            .then().log().all().extract();
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response, ExtractableResponse<Response> notFoundResponse) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(notFoundResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
