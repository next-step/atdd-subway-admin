package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.exception.LineDuplicateException;
import nextstep.subway.exception.NotFoundLineException;
import nextstep.subway.line.dto.LineRequest;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    public static final String LINES_PATH = "/lines";

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        //given
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "red");

        // when
        ExtractableResponse<Response> 신분당선_생성_응답 = 지하철_노선_생성_요청(신분당선);

        // then
        지하철_노선_생성됨(신분당선_생성_응답);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void duplicateLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "red");
        ExtractableResponse<Response> 신분당선_생성_응답 = 지하철_노선_생성_요청(신분당선);

        // when
        ExtractableResponse<Response> 중복_결과_응답 = 지하철_노선_생성_요청(신분당선);

        // then
        지하철_노선_생성_실패됨(중복_결과_응답);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "red");
        ExtractableResponse<Response> 신분당선_생성_응답 = 지하철_노선_생성_요청(신분당선);
        // 지하철_노선_등록되어_있음
        LineRequest 공항철도 = 지하철_노선_정보("공항철도", "blue");
        ExtractableResponse<Response> 공항철도_생성_응답 = 지하철_노선_생성_요청(공항철도);

        // when
        ExtractableResponse<Response> 노선_목록_응답 = 지하철_노선_목록_조회();

        // then
        지하철_노선_응답됨(노선_목록_응답);
        지하철_노선_목록_포함됨(노선_목록_응답, 신분당선_생성_응답, 공항철도_생성_응답);
    }

    @DisplayName("지하철 노선 목록이 없다.")
    @Test
    void notFoundLines() {
        // when
        ExtractableResponse<Response> 노선_목록_응답 = 지하철_노선_목록_조회();

        // then
        지하철_노선_존재하지_않음(노선_목록_응답);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "red");
        ExtractableResponse<Response> 신분당선_생성_응답 = 지하철_노선_생성_요청(신분당선);
        URI 신분당선_생성_응답_정보 = URI.create(신분당선_생성_응답.header("Location"));

        // when
        ExtractableResponse<Response> 신분당선_조회_응답 = 지하철_노선_조회_요청(신분당선_생성_응답_정보);

        // then
        지하철_노선_응답됨(신분당선_조회_응답);
    }

    @DisplayName("존재하지 않는 지하철 노선 조회")
    @Test
    void notFoundLine() {
        // given
        // 존재하지 않는 지하철 노선
        URI 존재하지_않는_지하철_노선_정보 = URI.create(LINES_PATH + "/2");

        // when
        ExtractableResponse<Response> 존재하지_않는_지하철_노선_조회_응답 = 지하철_노선_조회_요청(존재하지_않는_지하철_노선_정보);

        // then
        지하철_노선_존재하지_않음(존재하지_않는_지하철_노선_조회_응답);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "red");
        ExtractableResponse<Response> 신분당선_생성_응답 = 지하철_노선_생성_요청(신분당선);
        URI 신분당선_생성_응답_정보 = URI.create(신분당선_생성_응답.header("Location"));
        LineRequest 공항철도 = 지하철_노선_정보("공항철도", "blue");

        // when
        ExtractableResponse<Response> 신분당선_수정_응답 = 지하철_노선_수정_요청(신분당선_생성_응답_정보, 공항철도);

        // then
        지하철_노선_응답됨(신분당선_수정_응답);
    }

    @DisplayName("존재하지 않는 지하철 노선을 수정한다.")
    @Test
    void notFoundLineUpdate() {
        // given
        // 지하철_노선_존재하지_않음
        URI 존재하지_않는_노선 = URI.create(LINES_PATH + "/-1");
        LineRequest 경의중앙선 = 지하철_노선_정보("경의중앙선", "green");

        // when
        ExtractableResponse<Response> 신분당선_수정_응답 = 지하철_노선_수정_요청(존재하지_않는_노선, 경의중앙선);

        // then
        지하철_노선_존재하지_않음(신분당선_수정_응답);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "red");
        ExtractableResponse<Response> 신분당선_생성_응답 = 지하철_노선_생성_요청(신분당선);

        // when
        ExtractableResponse<Response> 신분당선_제거_응답 = 지하철_노선_제거_요청(신분당선_생성_응답);

        // then
        지하철_노선_삭제됨(신분당선_제거_응답);
    }

    private LineRequest 지하철_노선_정보(String name, String color) {
        return new LineRequest(name, color);
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest lineRequest) {
        return RestAssured
            .given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post(LINES_PATH)
            .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회() {
        return RestAssured
            .given().log().all()
            .when().get(LINES_PATH)
            .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(URI uri) {
        return RestAssured
            .given().log().all()
            .when().get(uri)
            .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(URI updateUri, LineRequest changeLine) {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(changeLine)
            .when().put(updateUri)
            .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(ExtractableResponse<Response> response) {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete(response.header("Location"))
            .then().log().all().extract();
    }

    private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> resultResponse,
        ExtractableResponse<Response> response1, ExtractableResponse<Response> response2) {
        List<Long> expectedLineIds = Arrays.asList(
            response1.jsonPath().getObject("id", Long.class),
            response2.jsonPath().getObject("id", Long.class)
        );
        List<Long> resultLineIds = resultResponse.jsonPath().getList("id", Long.class);
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getObject("message", String.class)).isEqualTo(
            LineDuplicateException.LINE_DUPLICATE);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void 지하철_노선_존재하지_않음(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getObject("message", String.class)).isEqualTo(
            NotFoundLineException.NOT_FOUND_LINE);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
