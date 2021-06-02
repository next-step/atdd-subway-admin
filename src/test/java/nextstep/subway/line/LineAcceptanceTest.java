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

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        LineRequest request = new LineRequest("신분당선", "bg-red-600");
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(request);

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest request = new LineRequest("신분당선", "bg-red-600");
        지하철_노선_등록되어_있음(request);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(request);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse createResponse1 = 지하철_노선_등록되어_있음(new LineRequest("신분당선", "bg-red-600"));
        // 지하철_노선_등록되어_있음
        LineResponse createResponse2 = 지하철_노선_등록되어_있음(new LineRequest("2호선", "bg-green-600"));

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> responseLines = 지하철_노선_목록_조회_요청();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(responseLines.statusCode()).isEqualTo(HttpStatus.OK.value());

        // 지하철_노선_목록_포함됨
        List<Long> expectedLineIds = Arrays.asList(createResponse1.getId(), createResponse2.getId()).stream()
                .collect(Collectors.toList());
        List<Long> resultLineIds = responseLines.jsonPath()
                .getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse request1 = 지하철_노선_등록되어_있음(new LineRequest("신분당선", "bg-red-600"));

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(request1.getId());

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse response = 지하철_노선_등록되어_있음(new LineRequest("2호선", "초록색"));

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> subwayLineResponse = 지하철_노선_수정_요청(response.getId(), new LineRequest("4호선", "파란색"));

        // then
        // 지하철_노선_수정됨
        assertThat(subwayLineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse request1 = 지하철_노선_등록되어_있음(new LineRequest("2호선", "초록색"));

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> deleteResponse = 지하철_노선_제거_요청(request1.getId());

        // then
        // 지하철_노선_삭제됨
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private LineResponse 지하철_노선_등록되어_있음(LineRequest request) {
        return 지하철_노선_생성_요청(request).jsonPath()
                .getObject(".", LineResponse.class);
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines")
                .then()
                .log()
                .all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/" + id)
                .then()
                .log()
                .all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(Long id, LineRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + id)
                .then().log().all()
                .extract();
    }
}
