package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesSubResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> extractableResponse = 지하철_노선_생성_요청(지하철_노선_파라미터("신분당선", "red"));

        // then
        지하철_노선_생성됨(extractableResponse);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        //given
        지하철_노선_생성("신분당선", "red");

        // when
        ExtractableResponse<Response> extractableResponse = 지하철_노선_생성_요청(지하철_노선_파라미터("신분당선", "red"));

        // then
        지하철_노선_이름충돌로_생성되지_않음(extractableResponse);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        //given
        LineResponse line1 = 지하철_노선_생성("신분당선", "red");
        LineResponse line2 = 지하철_노선_생성("분당선", "yellow");

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_조회됨(response, line1, line2);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        //given
        LineResponse lineResponse = 지하철_노선_생성("신분당선", "red");

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineResponse);

        // then
        지하철_노선_조회됨(response, lineResponse);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        LineResponse lineResponse = 지하철_노선_생성("신분당선", "red");

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(lineResponse);

        // then
        지하철_노선_수정됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        LineResponse lineResponse = 지하철_노선_생성("신분당선", "red");

        // when
        ExtractableResponse<Response> response = 지하철_노선_삭제_요청(lineResponse);

        // then
        지하철_노선_삭제됨(response);
    }

    private LineResponse 지하철_노선_생성(String name, String color) {
        Map<String, String> params = 지하철_노선_파라미터(name, color);
        ExtractableResponse<Response> extractableResponse = 지하철_노선_생성_요청(params);
        지하철_노선_생성됨(extractableResponse);
        return extractableResponse.jsonPath().getObject(".", LineResponse.class);
    }

    private Map<String, String> 지하철_노선_파라미터(String name, String color) {
        Map<String, String> givenParams = new HashMap<>();
        givenParams.put("name", name);
        givenParams.put("color", color);
        return givenParams;
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> extractableResponse) {
        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_노선_이름충돌로_생성되지_않음(ExtractableResponse<Response> extractableResponse) {
        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void 지하철_노선_목록_조회됨(ExtractableResponse<Response> response, LineResponse... lineResponses) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<LinesSubResponse> list = response.jsonPath().getList(".");
        assertThat(list.size()).isEqualTo(lineResponses.length);
    }

    private void 지하철_노선_조회됨(ExtractableResponse<Response> response, LineResponse lineResponse) {
        LinesSubResponse linesSubResponse = response.jsonPath().getObject(".", LinesSubResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(linesSubResponse.getName()).isEqualTo(lineResponse.getName());
        assertThat(linesSubResponse.getColor()).isEqualTo(lineResponse.getColor());
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        ExtractableResponse<Response> response =
                given()
                        .log().all()
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .when()
                        .get("/lines")
                        .then()
                        .log().all()
                        .extract();
        return response;
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(LineResponse lineResponse) {
        ExtractableResponse<Response> response =
                given()
                        .log().all()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .pathParam("lineId", lineResponse.getId())
                        .when()
                        .get("/lines/{lineId}")
                        .then()
                        .log().all()
                        .extract();
        return response;
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(LineResponse lineResponse) {
        Map<String, String> params = 지하철_노선_파라미터("구분당선", "blue");
        ExtractableResponse<Response> response =
                given()
                        .log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .pathParam("lineId", lineResponse.getId())
                        .when()
                        .put("/lines/{lineId}")
                        .then()
                        .log().all()
                        .extract();
        return response;
    }

    private ExtractableResponse<Response> 지하철_노선_삭제_요청(LineResponse lineResponse) {
        ExtractableResponse<Response> response =
                given()
                        .log().all()
                        .pathParam("lineId", lineResponse.getId())
                        .when()
                        .delete("/lines/{lineId}")
                        .then()
                        .log().all()
                        .extract();
        return response;
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, String> params) {
        ExtractableResponse<Response> response =
                given()
                        .log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when()
                        .post("/lines")
                        .then()
                        .log().all()
                        .extract();
        return response;
    }
}
