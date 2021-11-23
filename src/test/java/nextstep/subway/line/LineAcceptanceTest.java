package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("신분당선", "red");

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_생성_요청("신분당선", "red");

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("신분당선", "red");

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        지하철_노선_생성_요청("신분당선", "red");
        지하철_노선_생성_요청("2호선", "green");

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_조회됨(response);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청("신분당선", "red");
        String lineId = createResponse.jsonPath().get("id").toString();

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(Long.parseLong(lineId));

        // then
        지하철_노선_응답됨(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청("신분당선", "red");
        String lineId = createResponse.jsonPath().get("id").toString();

        // when
        LineRequest lineRequest = new LineRequest("2호선", "green");
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(Long.parseLong(lineId), lineRequest);

        // then
        지하철_노선_수정됨(response, lineRequest);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청("신분당선", "red");
        String lineId = createResponse.jsonPath().get("id").toString();

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(Long.parseLong(lineId));

        // then
        지하철_노선_삭제됨(response);
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(long lineId) {
        return RestAssured.given().log().all()
                .pathParam("lineId", lineId)
                .when()
                .get("/lines/{lineId}")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(long lineId, LineRequest lineRequest) {
        Map<String, String> params = new HashMap<>();
        params.put("name", lineRequest.getName());
        params.put("color", lineRequest.getColor());

        return RestAssured.given().log().all()
                .pathParam("lineId", lineId)
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/{lineId}")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(long lineId) {
        return RestAssured.given().log().all()
                .pathParam("lineId", lineId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/{lineId}")
                .then().log().all()
                .extract();
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response, LineRequest lineRequest) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
                , () -> assertThat(response.jsonPath().get("name").toString()).hasToString(lineRequest.getName())
                , () -> assertThat(response.jsonPath().get("color").toString()).hasToString(lineRequest.getColor())
        );
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void 지하철_노선_목록_조회됨(ExtractableResponse<Response> response) {
        assertAll(
                () -> 지하철_노선_목록_응답됨(response)
                , () -> 지하철_노선_목록_포함됨(response, Arrays.asList("신분당선", "2호선"))
        );
    }

    private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response, List<String> names) {
        assertThat(response.jsonPath().getList("name", String.class).containsAll(names)).isTrue();
    }
}
