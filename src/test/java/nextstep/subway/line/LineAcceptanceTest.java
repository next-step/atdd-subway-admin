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
    void createLine() {
        //given
        Map<String, String> params = getParams("bg-red-600", "신분당선");

        // when 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);

        // then 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        Map<String, String> params = getParams("bg-red-600", "신분당선");
        지하철_노선_등록되어_있음(params);

        // when 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);

        // then 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params1 = getParams("bg-red-600", "신분당선");
        ExtractableResponse<Response> createResponse1 = 지하철_노선_생성_요청(params1);

        // 지하철_노선_등록되어_있음
        Map<String, String> params2 = getParams("green darken-2", "7호선");
        ExtractableResponse<Response> createResponse2 = 지하철_노선_생성_요청(params2);

        // when 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_조회_됨(createResponse1, createResponse2, response);
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    private void 지하철_노선_목록_조회_됨(ExtractableResponse<Response> createResponse1, ExtractableResponse<Response> createResponse2, ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given 지하철_노선_등록되어_있음
        Map<String, String> params = getParams("bg-red-600", "신분당선");
        지하철_노선_등록되어_있음(params);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회_요청();

        // then 지하철_노선_응답됨

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body()).isNotNull();
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청() {
        return RestAssured.given().log().all()
                .when()
                .get("lines/1")
                .then().log().all()
                .extract();
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given 지하철_노선_등록되어_있음
        Map<String, String> params = getParams("bg-red-600", "신분당선");
        지하철_노선_등록되어_있음(params);

        // when 지하철_노선_수정_요청
        Map<String, String> updateParams = getParams("bg-blue-600", "구분당선");
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(updateParams);

        // then 지하철_노선_수정됨
        LineResponse finalResponse = response.jsonPath().getObject(".", LineResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(finalResponse.getName()).isEqualTo("구분당선");
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(Map<String, String> updateParams) {
        return RestAssured.given().log().all()
                .pathParam("id", "1")
                .body(updateParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("lines/{id}")
                .then().log().all()
                .extract();
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given 지하철_노선_등록되어_있음
        Map<String, String> params = getParams("bg-red-600", "신분당선");
        지하철_노선_등록되어_있음(params);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청();

        // then // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .pathParam("id", "1")
                .when()
                .delete("/lines/{id}")
                .then().log().all()
                .extract();
        return response;
    }

    private Map<String, String> getParams(String color, String name) {
        Map<String, String> params = new HashMap<>();
        params.put("color", color);
        params.put("name", name);
        return params;
    }

    private void 지하철_노선_등록되어_있음(Map<String, String> params) {
        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }
}
