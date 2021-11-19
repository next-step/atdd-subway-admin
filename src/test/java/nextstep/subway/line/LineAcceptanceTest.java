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

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성("신분당선", "red");

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }


    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_생성("2호선", "green");

        // when
        // 지하철_노선_생성_요청
        final ExtractableResponse<Response> response = 지하철_노선_생성("2호선", "red");

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        지하철_노선_생성("2호선", "green");
        지하철_노선_생성("신분당선", "red");

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_목록_조회();

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList(".", LineResponse.class).size()).isEqualTo(2);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_생성("2호선", "green");

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회(1L);

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getObject(".", LineResponse.class))
            .extracting("name", "column")
            .contains("2호선", "green");
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_생성("2호선", "green");

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = 지하철_노선_수정(1L, "신분당선", "red");

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_생성("2호선", "green");

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = 지하철_노선_삭제(1L);

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> 지하철_노선_생성(String name, String color) {
        return RestAssured
            .given().log().all()
            .body(new LineRequest(name, color))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회() {
        return RestAssured
            .given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_조회(Long lineId) {
        return RestAssured
            .given().log().all()
            .pathParam("id", lineId)
            .when()
            .get("/lines/{id}")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정(long lineId, String name, String color) {
        return RestAssured
            .given().log().all()
            .body(new LineRequest(name, color))
            .pathParam("id", lineId)
            .when()
            .put("/lines/{id}")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_삭제(long lineId) {
        return RestAssured
            .given().log().all()
            .pathParam("id", lineId)
            .when()
            .delete("/lines/{id}")
            .then().log().all()
            .extract();
    }
}
