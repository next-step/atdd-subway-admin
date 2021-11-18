package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.common.exception.ApiErrorMessage;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineResponseList;
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
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineRequest);

        // then
        // 지하철_노선_생성됨
        지하철_노선_생성됨(response);
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank()
        );
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");

        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음(lineRequest);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineRequest);

        // then
        // 지하철_노선_생성_실패됨
        지하철_노선_생성_실패됨(response);
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.as(ApiErrorMessage.class).getMessage())
                        .isEqualTo("중복된 노선을 추가할 수 없습니다.")
        );
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");
        LineResponse lineResponse = 지하철_노선_등록되어_있음(lineRequest);
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest2 = new LineRequest("2호선", "bg-green-600");
        LineResponse lineResponse2 = 지하철_노선_등록되어_있음(lineRequest2);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        // 지하철_노선_목록_응답됨
        지하철_노선_목록_응답됨(response);
        // 지하철_노선_목록_포함됨
        지하철_노선_목록에_포함_검증(response, lineResponse, lineResponse2);
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given()
                .log().all()
                .when()
                .get("/lines")
                .then()
                .log().all()
                .extract();
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");
        LineResponse lineResponse = 지하철_노선_등록되어_있음(lineRequest);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineResponse.getId());

        // then
        // 지하철_노선_응답됨
        지하철_노선_목록_응답됨(response);
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
        return RestAssured.given()
                .log().all()
                .when()
                .get(String.format("/lines/%s", id))
                .then()
                .log().all()
                .extract();
    }

    private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_목록에_포함_검증(final ExtractableResponse<Response> response, final LineResponse... lineResponses) {
        LineResponseList lineResponseList = response.as(LineResponseList.class);

        assertAll(
                () -> assertThat(lineResponseList).isNotNull(),
                () -> {
                    List<LineResponse> createdLines = Arrays.stream(lineResponses).collect(Collectors.toList());
                    assertThat(lineResponseList.getLineResponseList()).containsAll(createdLines);
                }
        );
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");
        LineResponse lineResponse = 지하철_노선_등록되어_있음(lineRequest);

        // when
        // 지하철_노선_수정_요청
        LineRequest updateLineRequest = new LineRequest("구분당선", "bg-blue-600");
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(lineResponse.getId(), updateLineRequest);
        LineResponse updatedLine = response.as(LineResponse.class);

        // then
        // 지하철_노선_수정됨
        지하철_노선_수정됨(lineResponse, updatedLine);
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(Long id, LineRequest lineRequest) {
        return RestAssured.given()
                .log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(String.format("/lines/%s", id))
                .then()
                .log().all()
                .extract();
    }

    private void 지하철_노선_수정됨(LineResponse lineResponse, LineResponse updatedLine) {
        assertAll(
                () -> assertThat(updatedLine.getId().equals(lineResponse.getId())).isTrue(),
                () -> assertThat(updatedLine.getName().equals(lineResponse.getName())).isFalse(),
                () -> assertThat(updatedLine.getColor().equals(lineResponse.getColor())).isFalse()
        );
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(lineRequest);

        // when
        // 지하철_노선_제거_요청
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(uri);

        // then
        // 지하철_노선_삭제됨
        지하철_노선_삭제됨(response);
    }

    private LineResponse 지하철_노선_등록되어_있음(final LineRequest request) {
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(request);
        return response.as(LineResponse.class);
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(final LineRequest lineRequest) {
        return RestAssured.given()
                .log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(final String uri) {
        return RestAssured.given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(uri)
                .then()
                .log().all()
                .extract();
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
