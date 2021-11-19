package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineFindResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("bg-red-600", "신분당선");

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_등록되어_있음("bg-red-600", "신분당선");

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("bg-red-600", "신분당선");

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        지하철_노선_등록되어_있음("bg-red-600", "신분당선");
        지하철_노선_등록되어_있음("bg-green-600", "2호선");

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(response);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        지하철_노선_등록되어_있음("bg-red-600", "신분당선");

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(1);

        // then
        지하철_노선_응답됨(response);
    }

    @DisplayName("조회한 노선이 존재하지 않을 경우 실패한다.")
    @Test
    void getLine_존재하지_않는_노선() {
        // given
        // 지하철_노선_등록되어_있지 않음

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(1);

        // then
        지하철_노선_조회_실패됨(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        지하철_노선_등록되어_있음("bg-red-600", "신분당선");

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청("bg-blue-600", "구분당선", 1);

        // then
        지하철_노선_수정됨(response);
    }

    @DisplayName("수정할 노선이 존재하지 않는 경우 실패한다.")
    @Test
    void updateLine_존재하지_않는_노선() {
        // given
        // 지하철_노선_등록되어_있지_않음

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청("bg-blue-600", "구분당선", 1);

        // then
        지하철_노선_수정_실패됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        지하철_노선_등록되어_있음("bg-red-600", "신분당선");

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(1);

        // then
        지하철_노선_삭제됨(response);
    }

    @DisplayName("제거할 노선이 존재하지 않는 경우")
    @Test
    void deleteLine_존재하지_않는_노선() {
        // given
        // 지하철_노선_등록되어_있지_않음

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(1);

        // then
        지하철_노선_삭제_실패됨(response);
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(String color, String name) {
        Map<String, String> params = new HashMap<>();
        params.put("color", color);
        params.put("name", name);

        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_노선_등록되어_있음(String color, String name) {
        Map<String, String> params = new HashMap<>();
        params.put("color", color);
        params.put("name", name);

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();

        지하철_노선_생성됨(response);
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all().extract();
        return response;
    }

    private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response) {
        List<LineFindResponse> lines = response.jsonPath().getList(".", LineFindResponse.class);
        assertThat(lines.size()).isEqualTo(2);
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(int id) {
        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/{id}", id)
                .then().log().all().extract();
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_조회_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(String color, String name, int id) {
        Map<String, String> params = new HashMap<>();
        params.put("color", color);
        params.put("name", name);
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{id}", id)
                .then().log().all().extract();
        return response;
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_수정_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(int id) {
        return RestAssured
                .given().log().all()
                .when().delete("/lines/{id}", id)
                .then().log().all().extract();
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void 지하철_노선_삭제_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}