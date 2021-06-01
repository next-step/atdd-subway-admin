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
        // when
        // 지하철_노선_생성_요청
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "red");

        ExtractableResponse<Response> response = RestAssured.given().log().all().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines").
                then().
                log().all().extract();
        
        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "red");

        RestAssured.given().log().all().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines").
                then().
                log().all().extract();

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = RestAssured.given().log().all().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines").
                then().
                log().all().extract();

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "red");

        RestAssured.given().log().all().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines").
                then().
                log().all().extract();

        // 지하철_노선_등록되어_있음
        params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "green");

        RestAssured.given().log().all().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines").
                then().
                log().all().extract();

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = RestAssured.given().log().all().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                get("/lines").
                then().
                log().all().extract();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        List<String> lineNames = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getName)
                .collect(Collectors.toList());
        assertThat(lineNames).isEqualTo(Arrays.asList("신분당선", "2호선"));
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "red");

        ExtractableResponse<Response> response = RestAssured.given().log().all().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines").
                then().
                log().all().extract();

        // when
        // 지하철_노선_조회_요청
        String savedUri = response.header("Location");
        response = RestAssured.given().log().all().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                get(savedUri).
                then().
                log().all().extract();

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
        assertThat(lineResponse.getName()).isEqualTo("신분당선");
        assertThat(lineResponse.getColor()).isEqualTo("red");
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> registerParams = new HashMap<>();
        registerParams.put("name", "신분당선");
        registerParams.put("color", "red");

        ExtractableResponse<Response> response = RestAssured.given().log().all().
                body(registerParams).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines").
                then().
                log().all().extract();

        String savedUri = response.header("Location");

        // when
        // 지하철_노선_수정_요청
        Map<String, String> editParams = new HashMap<>();
        editParams.put("name", "2호선");
        editParams.put("color", "green");

        response = RestAssured.given().log().all().
                body(editParams).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                put(savedUri).
                then().
                log().all().extract();

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
        assertThat(lineResponse.getName()).isEqualTo("2호선");
        assertThat(lineResponse.getColor()).isEqualTo("green");
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> registerParams = new HashMap<>();
        registerParams.put("name", "신분당선");
        registerParams.put("color", "red");

        ExtractableResponse<Response> response = RestAssured.given().log().all().
                body(registerParams).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines").
                then().
                log().all().extract();

        String savedUri = response.header("Location");

        // when
        // 지하철_노선_제거_요청
        response = RestAssured.given().log().all()
                .when()
                .delete(savedUri)
                .then().log().all()
                .extract();

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
