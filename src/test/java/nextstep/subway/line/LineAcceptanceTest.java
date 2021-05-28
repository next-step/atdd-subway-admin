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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        LineRequest params = new LineRequest("2호선", "green");
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
        LineResponse resultBody = result.as(LineResponse.class);
        assertThat(resultBody.getName()).isEqualTo(params.getName());
        assertThat(resultBody.getColor()).isEqualTo(params.getColor());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest params = new LineRequest("2호선", "green");
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
        LineRequest lineNumberTwo = new LineRequest("2호선", "green");
        ExtractableResponse<Response> createdLine1 = RestAssured.given().log().all()
            .body(lineNumberTwo)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        // 지하철_노선_등록되어_있음
        LineRequest lineNumberThree = new LineRequest("3호선", "orange");
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
        List<LineResponse> expect = Stream.of(createdLine1, createdLine2)
            .map(response -> response.as(LineResponse.class))
            .collect(Collectors.toList());
        List<LineResponse> actual = result.body().jsonPath().getList(".", LineResponse.class);
        assertThat(actual).isEqualTo(expect);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineNumberTwo = new LineRequest("2호선", "green");
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
        /// given
        // 지하철_노선_등록되어_있음
        LineRequest lineNumberTwo = new LineRequest("2호선", "green");
        ExtractableResponse<Response> createdResponse = RestAssured.given().log().all()
            .body(lineNumberTwo)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        String savedId = createdResponse.header("Location").split("/")[2];

        // when
        // 지하철_노선_수정_요청
        LineRequest lineNumberTwentyTwo = new LineRequest("22호선", "lightGreen");
        ExtractableResponse<Response> result = RestAssured.given().log().all()
            .pathParam("id", String.valueOf(savedId))
            .body(lineNumberTwentyTwo)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/{id}")
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_수정됨
        assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> updatedSearch = RestAssured.given().log().all()
            .pathParam("id", savedId)
            .when()
            .get("/lines/{id}")
            .then().log().all()
            .extract();
        LineResponse updatedLine = updatedSearch.jsonPath().getObject(".", LineResponse.class);
        assertThat(updatedLine.getColor()).isEqualTo("lightGreen");
        assertThat(updatedLine.getName()).isEqualTo("22호선");
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineNumberTwo = new LineRequest("2호선", "green");
        ExtractableResponse<Response> createdResponse = RestAssured.given().log().all()
            .body(lineNumberTwo)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        String savedId = createdResponse.header("Location").split("/")[2];

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> result = RestAssured.given().log().all()
            .pathParam("id", savedId)
            .when()
            .delete("/lines/{id}")
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_삭제됨
        assertThat(result.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        ExtractableResponse<Response> deletedSearch = RestAssured.given().log().all()
            .pathParam("id", savedId)
            .when()
            .get("/lines/{id}")
            .then().log().all()
            .extract();
        assertThat(deletedSearch.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
