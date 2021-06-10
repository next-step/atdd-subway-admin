package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("경의선", "blue");

        // then
        // 지하철_노선_생성됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        Assertions.assertThat(response.statusCode()).isNotEqualTo(HttpStatus.CONFLICT.value());
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(final String name, final String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음("경의선", "blue");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("경의선", "blue");

        // then
        // 지하철_노선_생성_실패됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
        Assertions.assertThat(response.statusCode()).isNotEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음("경의선", "blue");
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음("4호선", "green");

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        // 지하철_노선_목록_응답됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(response.statusCode()).isNotEqualTo(HttpStatus.NOT_FOUND.value());
        // 값 검증
        List<LineResponse> lineResponses = response.jsonPath().getList(".", LineResponse.class);
        Assertions.assertThat(lineResponses)
                .hasSize(2)
                .extracting("name").contains("경의선", "4호선");
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all().extract();
        return response;
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음("경의선", "blue");
        지하철_노선_등록되어_있음("4호선", "green");

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(1L);

        // then
        // 지하철_노선_응답됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(response.statusCode()).isNotEqualTo(HttpStatus.NOT_FOUND.value());
        // 값 검증
        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
        Assertions.assertThat(lineResponse.getName()).isEqualTo("경의선");

    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(final Long id) {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/lines/{id}", id)
                .then().log().all().extract();
        return response;
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음("경의선", "blue");

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(1L, "경의선", "red");

        // then
        // 지하철_노선_수정됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(response.statusCode()).isNotEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(final Long id, final String name, final String colorToModify) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", colorToModify);

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{id}", id)
                .then().log().all().extract();
        return response;
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음("경의선", "blue");

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(1L);

        // then
        // 지하철_노선_삭제됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(response.statusCode()).isNotEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(final Long id) {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().delete("/lines/{id}", id)
                .then().log().all().extract();
        return response;
    }

    void 지하철_노선_등록되어_있음(final String name, final String color) {
        Map<String, String> alreadyParams = new HashMap<>();
        alreadyParams.put("name", name);
        alreadyParams.put("color", color);

        ExtractableResponse<Response> alreadyResponse = RestAssured
                .given().log().all()
                .body(alreadyParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();

        Assertions.assertThat(alreadyResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }
}
