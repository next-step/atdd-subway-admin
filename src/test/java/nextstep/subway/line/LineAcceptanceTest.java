package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> params = createDefaultParams();

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private Map<String, String> createDefaultParams() {
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        return params;
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = createDefaultParams();
        createTestLineData(params);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("lines")
                .then().log().all()
                .extract();

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params1 = createDefaultParams();
        createTestLineData(params1);
        // 지하철_노선_등록되어_있음
        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-red-600");
        params2.put("name", "2호선");
        createTestLineData(params2);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        List<String> lineNames = response
                .body()
                .jsonPath()
                .getList("$", LineResponse.class)
                .stream()
                .map(LineResponse::getName)
                .collect(Collectors.toList());
        assertThat(lineNames).contains("신분당선");
        assertThat(lineNames).contains("2호선");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = createDefaultParams();
        ExtractableResponse<Response> createResponse = createTestLineData(params);
        String createdId = createResponse.header("Location").split("/")[2];

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + createdId)
                .then().log().all()
                .extract();

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        String expected = response.body().jsonPath().getString("$");
        assertThat(expected).contains("신분당선");
    }

    @DisplayName("노선 id가 없는 지하철 노선을 조회한다.")
    @Test
    void getLine2() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = createDefaultParams();
        createTestLineData(params);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/100")
                .then().log().all()
                .extract();

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = createDefaultParams();
        ExtractableResponse<Response> createResponse = createTestLineData(params);
        String createdId = createResponse.header("Location").split("/")[2];

        // when
        // 지하철_노선_수정_요청
        Map<String, String> putParams = new HashMap<>();
        putParams.put("color", "bg-blue-600");
        putParams.put("name", "구분당선");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(putParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + createdId)
                .then().log().all()
                .extract();

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = createDefaultParams();
        ExtractableResponse<Response> createResponse = createTestLineData(params);
        String createdId = createResponse.header("Location").split("/")[2];

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/" + createdId)
                .then().log().all()
                .extract();

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> createTestLineData(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }
}
