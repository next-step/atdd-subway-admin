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

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        LineRequest lineRequest = new LineRequest("1호선", "blue");

        // when
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(lineRequest);
        // then
        지하철_노선_생성됨(createResponse, lineRequest);
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> createResponse, LineRequest lineRequest) {
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        LineResponse lineResponse = createResponse.jsonPath().getObject(".", LineResponse.class);
        assertThat(lineResponse.getName()).isEqualTo(lineRequest.getName());
        assertThat(lineResponse.getColor()).isEqualTo(lineRequest.getColor());
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest lineRequest) {
        return RestAssured.given().log().all()
          .accept(MediaType.ALL_VALUE)
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .body(lineRequest)
          .when()
          .post("/lines")
          .then().log().all().extract();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        LineRequest lineRequest = new LineRequest("2호선", "green");
        지하철_노선_생성_요청(lineRequest);

        // when
        ExtractableResponse<Response> errorResponse = 지하철_노선_생성_요청(lineRequest);

        // then
        지하철_노선_생성_실패됨(errorResponse);
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> errorResponse) {
        assertThat(errorResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        지하철_노선_생성_요청(new LineRequest("1호선", "blue"));
        지하철_노선_생성_요청(new LineRequest("2호선", "green"));

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_조회됨(response);
    }

    private void 지하철_노선_목록_조회됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
          .given().log().all()
          .accept(MediaType.APPLICATION_JSON_VALUE)
          .when().get("/lines")
          .then().log().all()
          .extract();
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        지하철_노선_생성_요청(new LineRequest("1호선", "blue"));

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청();

        // then
        지하철_노선_응답됨(response);
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getObject(".", LineResponse.class).getName()).isEqualTo("1호선");
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청() {
        return RestAssured
          .given().log().all()
          .accept(MediaType.APPLICATION_JSON_VALUE)
          .when().get("/lines/1")
          .then().log().all()
          .extract();
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        지하철_노선_생성_요청(new LineRequest("1호선", "blue"));

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청();

        // then
        지하철_노선_수정됨(response);
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청() {
        return RestAssured
          .given().log().all()
          .body(new LineRequest("분당선", "bg-blue-600"))
          .accept(MediaType.ALL_VALUE)
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .when().put("/lines/1")
          .then().log().all()
          .extract();
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        지하철_노선_생성_요청(new LineRequest("1호선", "blue"));

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청();

        // then
        지하철_노선_삭제됨(response);
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청() {
        return RestAssured
          .given().log().all()
          .accept(MediaType.ALL_VALUE)
          .when().delete("/lines/1")
          .then().log().all()
          .extract();
    }
}
