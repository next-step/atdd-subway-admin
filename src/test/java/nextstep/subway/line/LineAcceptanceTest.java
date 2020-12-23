package nextstep.subway.line;

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

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

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
        ExtractableResponse<Response> createdResponse1 = 지하철_노선_생성_요청("신분당선", "red");
        ExtractableResponse<Response> createdResponse2 = 지하철_노선_생성_요청("1호선", "blue");

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_응답됨(response);
        지하철_노선_목록_포함됨(response, createdResponse1, createdResponse2);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createdResponse = 지하철_노선_생성_요청("신분당선", "red");

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createdResponse);

        // then
        지하철_노선_응답됨(response);
        지하철_노선_조회_응답됨(createdResponse, response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createdResponse = 지하철_노선_생성_요청("신분당선", "red");
        String changeStation = "1호선";
        String changeColor = "blue";

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(createdResponse, changeStation, changeColor);

        // then
        지하철_노선_응답됨(response);
        지하철_노선_수정됨(response, changeStation, changeColor);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createdResponse = 지하철_노선_생성_요청("신분당선", "red");

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(createdResponse);

        // then
        지하철_노선_삭제됨(response);
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(final String station, final String color) {
        LineRequest lineRequest = new LineRequest(station, color);
        return given().log().all()
                .body(lineRequest)
                .accept(MediaType.ALL_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(final ExtractableResponse<Response> createdResponse) {
        String uri = createdResponse.header("Location");

        return given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(uri)
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines")
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(final ExtractableResponse<Response> createdResponse,
                                                       final String station,
                                                       final String color) {
        String uri = createdResponse.header("Location");
        LineRequest lineRequest = new LineRequest(station, color);
        return given().log().all()
                .body(lineRequest)
                .accept(MediaType.ALL_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(final ExtractableResponse<Response> createdResponse) {
        String uri = createdResponse.header("Location");
        return given().log().all()
                .accept(MediaType.ALL_VALUE)
                .when()
                .delete(uri)
                .then()
                .log().all()
                .extract();
    }

    private void 지하철_노선_응답됨(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
    }

    private void 지하철_노선_조회_응답됨(final ExtractableResponse<Response> createdResponse,
                               final ExtractableResponse<Response> response) {
        assertThat(createdResponse.as(LineResponse.class)).isEqualTo(response.as(LineResponse.class));
    }

    private void 지하철_노선_목록_포함됨(final ExtractableResponse<Response> response,
                               final ExtractableResponse<Response>... createdResponses) {
        LineResponse[] lineResponses = Arrays.stream(createdResponses)
                .map(createdResponse -> createdResponse.as(LineResponse.class))
                .toArray(LineResponse[]::new);
        assertThat(response.as(LineResponse[].class)).contains(lineResponses);
    }

    private void 지하철_노선_생성됨(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.as(LineResponse.class)).isNotNull();
    }

    private void 지하철_노선_수정됨(final ExtractableResponse<Response> response,
                            final String changeStation,
                            final String changeColor) {
        assertThat(response.as(LineResponse.class).getName()).isEqualTo(changeStation);
        assertThat(response.as(LineResponse.class).getColor()).isEqualTo(changeColor);
    }

    private void 지하철_노선_삭제됨(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void 지하철_노선_생성_실패됨(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
