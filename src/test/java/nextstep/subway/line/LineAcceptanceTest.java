package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private static final LineRequest 노선_2호선_생성_요청값 = new LineRequest("2호선", "green lighten-1");
    private static final LineRequest 노선_4호선_생성_요청값 = new LineRequest("4호선", "blue lighten-1");

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(노선_2호선_생성_요청값);

        // then
        지하철_노선_생성됨(response);
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest lineRequest) {
        return RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        지하철_노선_등록되어_있음(노선_2호선_생성_요청값);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(노선_2호선_생성_요청값);

        // then
        지하철_노선_생성_실패됨(response);
    }

    private LineResponse 지하철_노선_등록되어_있음(LineRequest lineRequest) {
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineRequest);
        return response.jsonPath().getObject(".", LineResponse.class);
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        LineResponse 노선_2호선_생성_응답 = 지하철_노선_등록되어_있음(노선_2호선_생성_요청값);
        LineResponse 노선_4호선_생성_응답 = 지하철_노선_등록되어_있음(노선_4호선_생성_요청값);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_응답됨(response);
        지하철_노석_목록_포함됨(response, 노선_2호선_생성_응답, 노선_4호선_생성_응답);
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();
    }

    private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노석_목록_포함됨(ExtractableResponse<Response> response, LineResponse ...createLineResponses) {
        List<Long> expectedLineIds = Stream.of(createLineResponses)
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        List<Long> actualLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        assertThat(actualLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        LineResponse 노선_2호선_생성_응답 = 지하철_노선_등록되어_있음(노선_2호선_생성_요청값);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(노선_2호선_생성_응답.getId());

        // then
        지하철_노선_응답됨(response);
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
        return RestAssured.given().log().all()
            .when()
            .get("/lines/{lineId}", id)
            .then().log().all()
            .extract();
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("생성되지 않은 지하철 노선을 조회한다.")
    @Test
    void getNotCreatedLine() {
        // given
        Long unknownId = 7L;

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(unknownId);

        // then
        지하철_노선_찾지_못함(response);
    }

    private void 지하철_노선_찾지_못함(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        LineResponse 노선_2호선_생성_응답 = 지하철_노선_등록되어_있음(노선_2호선_생성_요청값);

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(노선_2호선_생성_응답.getId(), 노선_4호선_생성_요청값);

        // then
        지하철_노선_수정됨(response);
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(Long id, LineRequest lineRequest) {
        return RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/{lineId}", id)
            .then().log().all()
            .extract();
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("생성되지 않은 지하철 노선을 수정한다.")
    @Test
    void updatedNotCreatedLine() {
        // given
        Long unknownId = 7L;

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(unknownId, 노선_4호선_생성_요청값);

        // then
        지하철_노선_찾지_못함(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        LineResponse 노선_2호선_생성_응답 = 지하철_노선_등록되어_있음(노선_2호선_생성_요청값);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(노선_2호선_생성_응답.getId());

        // then
        지하철_노선_삭제됨(response);
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(Long id) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete("/lines/{lineId}", id)
            .then().log().all()
            .extract();
        return response;
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
