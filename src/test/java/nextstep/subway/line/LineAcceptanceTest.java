package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    Map<String, String> params = new HashMap<>();

    @BeforeEach
    void init() {
        params.put("name", "2호선");
        params.put("color", "bg-red-600");
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음(params);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse1 = 지하철_노선_생성_요청(params);

        // 지하철_노선_등록되어_있음
        params.put("name", "3호선");
        params.put("color", "bg-yellow-600");
        ExtractableResponse<Response> createResponse2 = 지하철_노선_생성_요청(params);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // 지하철_노선_목록_포함됨
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(this::지하철_노선_응답_아이디)
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath()
                .getList(".", LineResponse.class)
                .stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(params);

        // when
        // 지하철_노선_조회_요청
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(uri);

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertAll(
                () -> assertThat(지하철_노선_응답_아이디(response)).isEqualTo(지하철_노선_응답_아이디(createResponse)),
                () -> assertThat(지하철_노선_응답_이름(response)).isEqualTo(지하철_노선_응답_이름(createResponse)),
                () -> assertThat(지하철_노선_응답_색상(response)).isEqualTo(지하철_노선_응답_색상(createResponse))
        );
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(params);

        // when
        // 지하철_노선_수정_요청
        params.clear();
        params.put("name", "3호선");
        params.put("color", "bg-yellow-600");
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(params, uri);

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertAll(
                () -> assertThat(지하철_노선_응답_아이디(response)).isEqualTo(지하철_노선_응답_아이디(createResponse)),
                () -> assertThat(지하철_노선_응답_이름(response)).isEqualTo("3호선"),
                () -> assertThat(지하철_노선_응답_색상(response)).isEqualTo("bg-yellow-600")
        );

    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(params);

        // when
        // 지하철_노선_제거_요청
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(uri);

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, String> params) {
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    private void 지하철_노선_등록되어_있음(Map<String, String> params) {
        RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(String uri) {
        return RestAssured
                .given().log().all()
                .when().get(uri)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(Map<String, String> params, String uri) {
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(uri)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(String uri) {
        return RestAssured
                .given().log().all()
                .when().delete(uri)
                .then().log().all().extract();
    }

    private Long 지하철_노선_응답_아이디(ExtractableResponse<Response> response) {
        return response.jsonPath()
                .getObject(".", LineResponse.class)
                .getId();
    }

    private String 지하철_노선_응답_이름(ExtractableResponse<Response> response) {
        return response.jsonPath()
                .getObject(".", LineResponse.class)
                .getName();
    }

    private String 지하철_노선_응답_색상(ExtractableResponse<Response> response) {
        return response.jsonPath()
                .getObject(".", LineResponse.class)
                .getColor();
    }

}
