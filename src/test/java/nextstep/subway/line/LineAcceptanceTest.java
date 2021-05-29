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
        String toCreateName = "2호선";
        String toCreateColor = "green";
        ExtractableResponse<Response> result = 지하철_노선_등록되어_있음(toCreateName, toCreateColor);
        // then
        // 지하철_노선_생성됨
        지하철_노선_생성됨(result, toCreateName, toCreateColor);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        String toCreateName = "2호선";
        String toCreateColor = "green";
        지하철_노선_등록되어_있음(toCreateName, toCreateColor);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> result = 지하철_노선_등록되어_있음(toCreateName, toCreateColor);

        // then
        // 지하철_노선_생성_실패됨
        지하철_노선_생성_실패됨(result);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> expect1 = 지하철_노선_등록되어_있음("2호선", "green");
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> expect2 = 지하철_노선_등록되어_있음("3호선", "orange");

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> result = 노선_목록을_조회한다();

        // then
        // 지하철_노선_목록_응답됨
        지하철_노선_목록_응답됨(result);
        // 지하철_노선_목록_포함됨
        지하철_노선_목록_포함됨(result, expect1, expect2);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createdLine1 = 지하철_노선_등록되어_있음("2호선", "green");
        Long savedId = 등록된_노선_ID(createdLine1);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> result = 노선_단건을_조회한다(savedId);

        // then
        // 지하철_노선_응답됨
        지하철_노선_응답됨(result, savedId);

    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        /// given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createdResponse = 지하철_노선_등록되어_있음("2호선", "green");
        Long savedId = 등록된_노선_ID(createdResponse);

        // when
        // 지하철_노선_수정_요청
        LineRequest lineNumberTwentyTwo = new LineRequest("22호선", "lightGreen");
        ExtractableResponse<Response> result = 노선을_수정한다(savedId, lineNumberTwentyTwo);

        // then
        // 지하철_노선_수정됨
        지하철_노선_수정됨(result, savedId);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createdResponse = 지하철_노선_등록되어_있음("2호선", "green");
        Long savedId = 등록된_노선_ID(createdResponse);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> result = 노선을_제거한다(savedId);


        // then
        // 지하철_노선_삭제됨
        지하철_노선_삭제됨(result, savedId);
    }

    private ExtractableResponse<Response> 지하철_노선_등록되어_있음(String name, String color) {
        LineRequest params = new LineRequest(name, color);
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 노선_목록을_조회한다() {
        return RestAssured.given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("lines")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 노선_단건을_조회한다(Long id) {
        return RestAssured.given().log().all()
            .pathParam("id", id)
            .when()
            .get("/lines/{id}")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 노선을_수정한다(Long id, LineRequest lineRequest) {
        return RestAssured.given().log().all()
            .pathParam("id", id)
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/{id}")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 노선을_제거한다(Long id) {
        return RestAssured.given().log().all()
            .pathParam("id", id)
            .when()
            .delete("/lines/{id}")
            .then().log().all()
            .extract();
    }

    private Long 등록된_노선_ID(ExtractableResponse<Response> createdResponse) {
        return createdResponse.as(LineResponse.class).getId();
    }

    private void 응답이_예상한_상태_코드를_갖는다(ExtractableResponse<Response> response, HttpStatus expect) {
        assertThat(response.statusCode()).isEqualTo(expect.value());
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> result, String toCreateName, String toCreateColor) {
        응답이_예상한_상태_코드를_갖는다(result, HttpStatus.CREATED);
        assertThat(result.header("Location")).isNotBlank();
        LineResponse resultBody = result.as(LineResponse.class);
        assertThat(resultBody.getName()).isEqualTo(toCreateName);
        assertThat(resultBody.getColor()).isEqualTo(toCreateColor);
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> result) {
        응답이_예상한_상태_코드를_갖는다(result, HttpStatus.BAD_REQUEST);
    }

    private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> result) {
        응답이_예상한_상태_코드를_갖는다(result, HttpStatus.OK);
    }

    private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> result, ExtractableResponse<Response>... expects) {
        List<LineResponse> expect = Stream.of(expects)
            .map(response -> response.as(LineResponse.class))
            .collect(Collectors.toList());
        List<LineResponse> actual = result.body().jsonPath().getList(".", LineResponse.class);
        assertThat(actual).isEqualTo(expect);
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> result, Long expectId) {
        응답이_예상한_상태_코드를_갖는다(result, HttpStatus.OK);
        LineResponse resultBody = result.jsonPath().getObject(".", LineResponse.class);
        assertThat(resultBody.getId()).isEqualTo(expectId);
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> result, Long savedId) {
        응답이_예상한_상태_코드를_갖는다(result, HttpStatus.OK);
        ExtractableResponse<Response> updatedSearch = 노선_단건을_조회한다(savedId);

        LineResponse updatedLine = updatedSearch.jsonPath().getObject(".", LineResponse.class);
        assertThat(updatedLine.getColor()).isEqualTo("lightGreen");
        assertThat(updatedLine.getName()).isEqualTo("22호선");
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> result, Long savedId) {
        응답이_예상한_상태_코드를_갖는다(result, HttpStatus.NO_CONTENT);
        ExtractableResponse<Response> deletedSearch = 노선_단건을_조회한다(savedId);
        응답이_예상한_상태_코드를_갖는다(deletedSearch, HttpStatus.NOT_FOUND);
    }
}
