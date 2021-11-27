package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final Map<String, String> LINE1 = new HashMap<>();
    private static final Map<String, String> LINE2 = new HashMap<>();

    @BeforeAll
    private static void setUpBeforeAll() {
        LINE1.put("name", "1호선");
        LINE1.put("color", "blue");
        LINE2.put("name", "2호선");
        LINE2.put("color", "green");
    }

    private ExtractableResponse<Response> 지하철_노선_등록되어_있음_1호선() {
        return 지하철_노선_생성_요청(LINE1);
    }

    private ExtractableResponse<Response> 지하철_노선_등록되어_있음_2호선() {
        return 지하철_노선_생성_요청(LINE2);
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(Map requestParam) {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestParam)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(String uri) {
        return RestAssured
            .given().log().all()
            .when()
            .get(uri)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
            .given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(String uri, LineRequest request) {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .when()
            .put(uri)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(String uri) {
        return RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();
    }

    private void 응답_코드_검증(ExtractableResponse<Response> response, HttpStatus status) {
        assertThat(response.statusCode()).isEqualTo(status.value());
    }

    private void 지하철_노선_목록_포함됨(List<ExtractableResponse<Response>> expectedList, ExtractableResponse<Response> actual) {
        List<Long> expectedLineIds = expectedList.stream()
            .map(createResponse ->
                Long.parseLong(createResponse
                    .header("Location")
                    .split("/")[2]))
            .collect(Collectors.toList());
        List<Long> actualLineIds = actual.jsonPath()
            .getList(".", LineResponse.class)
            .stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        assertThat(actualLineIds).containsAll(expectedLineIds);
    }

    private void 지하철_노선_조회됨(Map<String, String> expected, ExtractableResponse<Response> response) {
        지하철_노선_일치함(expected, response);
    }

    private void 지하철_노선_수정됨(Map<String, String> expected, ExtractableResponse<Response> response) {
        지하철_노선_일치함(expected, response);
    }

    private void 지하철_노선_일치함(Map<String, String> expected, ExtractableResponse<Response> response) {
        LineResponse actual = response.jsonPath().getObject(".", LineResponse.class);
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo(expected.get("name"));
        assertThat(actual.getColor()).isEqualTo(expected.get("color"));
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(LINE1);

        // then
        응답_코드_검증(response, HttpStatus.CREATED);
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_등록되어_있음_1호선();

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(LINE1);

        // then
        응답_코드_검증(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> createResponse1 = 지하철_노선_등록되어_있음_1호선();
        ExtractableResponse<Response> createResponse2 = 지하철_노선_등록되어_있음_2호선();

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        응답_코드_검증(response, HttpStatus.OK);
        지하철_노선_목록_포함됨(Arrays.asList(createResponse1, createResponse2), response);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음_1호선();

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createResponse.header("Location"));

        // then
        응답_코드_검증(response, HttpStatus.OK);
        지하철_노선_조회됨(LINE1, response);
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회한다.")
    @Test
    void getLine_Fail() {

        // when
        String uri = "/lines/1";
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(uri);

        // then
        응답_코드_검증(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음_1호선();

        // when
        String uri = createResponse.header("Location");
        LineRequest lineRequest = new LineRequest(LINE2.get("name"), LINE2.get("color"));
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(uri, lineRequest);

        // then
        응답_코드_검증(response, HttpStatus.OK);
        지하철_노선_수정됨(LINE2, response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음_1호선();

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
