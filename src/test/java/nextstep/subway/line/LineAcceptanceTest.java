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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "green");
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> result = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        // then
        // 지하철_노선_생성됨
        assertThat(result.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(result.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "green");
        RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> result = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_생성_실패됨
        assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> lineNumberTwo = new HashMap<>();
        lineNumberTwo.put("name", "2호선");
        lineNumberTwo.put("color", "green");
        ExtractableResponse<Response> createdLine1 = RestAssured.given().log().all()
            .body(lineNumberTwo)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        // 지하철_노선_등록되어_있음
        Map<String, String> lineNumberThree = new HashMap<>();
        lineNumberTwo.put("name", "3호선");
        lineNumberTwo.put("color", "orange");
        ExtractableResponse<Response> createdLine2 = RestAssured.given().log().all()
            .body(lineNumberThree)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> result = RestAssured.given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("lines")
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        List<Long> expect = Stream.of(createdLine1, createdLine2)
            .map(response -> Long.parseLong(response.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> actual = result.body().jsonPath().getList(".", LineResponse.class)
            .stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        assertThat(actual).isEqualTo(expect);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> lineNumberTwo = new HashMap<>();
        lineNumberTwo.put("name", "2호선");
        lineNumberTwo.put("color", "green");
        ExtractableResponse<Response> createdLine1 = RestAssured.given().log().all()
            .body(lineNumberTwo)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        String savedId = createdLine1.header("Location").split("/")[2];

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> result = RestAssured.given().log().all()
            .pathParam("id", savedId)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines/{id}")
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_응답됨
        assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse resultBody = result.jsonPath().getObject(".", LineResponse.class);
        assertThat(resultBody.getId()).isEqualTo(Long.valueOf(savedId));
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_수정_요청

        // then
        // 지하철_노선_수정됨
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_제거_요청

        // then
        // 지하철_노선_삭제됨
    }
}
