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
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    private final String BASE_LINE_URL = "/lines";

    private LineRequest 신분당선_요청_데이터() {
        return new LineRequest("신분당선", "bg-red-600");
    }

    private LineRequest 구분당선_요청_데이터() {
        return new LineRequest("구분당선", "bg-blue-600");
    }

    private LineRequest 이호선_요청_데이터() {
        return new LineRequest("2호선", "bg-green-600");
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest lineRequest) {
        return RestAssured.given()
                .log()
                .all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(BASE_LINE_URL)
                .then()
                .log()
                .all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return 지하철_노선_목록_조회_요청(BASE_LINE_URL);
    }

    private ExtractableResponse<Response> 특정_지하철_노선_조회_요청(LineResponse lineResponse) {
        return 지하철_노선_목록_조회_요청(BASE_LINE_URL + "/" + lineResponse.getId());
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청(String requestUrl) {
        return RestAssured.given()
                .log()
                .all()
                .when()
                .get(requestUrl)
                .then()
                .log()
                .all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(LineResponse 신분당선) {
        return RestAssured.given()
                .log()
                .all()
                .body(구분당선_요청_데이터())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(BASE_LINE_URL + "/" + 신분당선.getId())
                .then()
                .log()
                .all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(LineResponse 신분당선) {
        return RestAssured.given()
                .log()
                .all()
                .when()
                .delete(BASE_LINE_URL + "/" + 신분당선.getId())
                .then()
                .log()
                .all()
                .extract();
    }

    private LineResponse 지하철_노선_등록되어_있음(LineRequest lineRequest) {
        return 지하철_노선_생성_요청(lineRequest)
                .as(LineResponse.class);
    }

    private void 지하철_노선_조회_데이터_확인(ExtractableResponse<Response> response, LineResponse... lineResponses) {
        List<Long> expectedLineIds = Arrays.stream(lineResponses)
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        List<Long> resultLineIds = response.jsonPath()
                .getList(".", LineResponse.class)
                .stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultLineIds)
                .containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        LineRequest lineRequest = 신분당선_요청_데이터();

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineRequest);

        // then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location"))
                        .isNotBlank()
        );
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_생성_요청(신분당선_요청_데이터());

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(신분당선_요청_데이터());

        // then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        LineResponse 신분당선 = 지하철_노선_등록되어_있음(신분당선_요청_데이터());
        LineResponse 이호선 = 지하철_노선_등록되어_있음(이호선_요청_데이터());

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.OK.value()),
                () -> 지하철_노선_조회_데이터_확인(response, 신분당선, 이호선)
        );
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        LineResponse 신분당선 = 지하철_노선_등록되어_있음(신분당선_요청_데이터());

        // when
        ExtractableResponse<Response> response = 특정_지하철_노선_조회_요청(신분당선);

        // then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.OK.value()),
                () -> 지하철_노선_조회_데이터_확인(response, 신분당선)
        );
    }

    @DisplayName("없는 지하철 노선을 조회한다.")
    @Test
    void getUnknownLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청(BASE_LINE_URL + "/1");

        // then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        LineResponse 신분당선 = 지하철_노선_등록되어_있음(신분당선_요청_데이터());

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(신분당선);

        // then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        LineResponse 신분당선 = 지하철_노선_등록되어_있음(신분당선_요청_데이터());

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(신분당선);

        // then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
