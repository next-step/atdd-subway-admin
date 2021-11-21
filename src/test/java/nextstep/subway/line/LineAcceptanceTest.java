package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        LineRequest request = new LineRequest("신분당선", "bg-red-600");

        ExtractableResponse<Response> response = 지하철_노선_생성(request, "/lines");

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        assertThat(response.body().jsonPath().getString("name")).isEqualTo("신분당선");
        assertThat(response.body().jsonPath().getString("color")).isEqualTo("bg-red-600");
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest request = new LineRequest("신분당선", "bg-red-600");
        지하철_노선_생성(request, "/lines");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성(request, "/lines");

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        LineRequest request1 = new LineRequest("신분당선", "bg-red-600");
        LineRequest request2 = new LineRequest("2호선", "bg-green-600");
        지하철_노선_생성(request1, "/lines");
        지하철_노선_생성(request2, "/lines");

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회("/lines");

        List<LineResponse> lines = response.body().jsonPath().getList(".", LineResponse.class);

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lines).isNotNull();
        assertThat(lines).hasSize(2);

        // 지하철_노선_목록_포함됨
        List<String> lineNames = lines.stream()
            .map(LineResponse::getName)
            .collect(Collectors.toList());

        assertThat(lineNames).containsExactly("신분당선", "2호선");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest request = new LineRequest("신분당선", "bg-red-600");
        지하철_노선_생성(request, "/lines");

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회("/lines/1");

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getString("name")).isEqualTo("신분당선");
        assertThat(response.body().jsonPath().getString("color")).isEqualTo("bg-red-600");
    }

    @DisplayName("지하철 노선을 조회 할 때 없는 노선을 조회하는 경우")
    @Test
    void getLineNotExists() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest request = new LineRequest("신분당선", "bg-red-600");
        지하철_노선_생성(request, "/lines");

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회("/lines/2");

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest request = new LineRequest("신분당선", "bg-red-600");
        지하철_노선_생성(request, "/lines");

        // when
        // 지하철_노선_수정_요청
        LineRequest modifyRequest = new LineRequest("구분당선", "bg-red-600");
        ExtractableResponse<Response> response = 지하철_노선_수정(modifyRequest, "/lines/1");

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getString("name")).isEqualTo("구분당선");
    }

    @DisplayName("없는 지하철 노선을 수정 할 때")
    @Test
    void updateLineNotExists() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest request = new LineRequest("신분당선", "bg-red-600");
        지하철_노선_생성(request, "/lines");

        // when
        // 지하철_노선_수정_요청
        LineRequest modifyRequest = new LineRequest("구분당선", "bg-red-600");
        ExtractableResponse<Response> response = 지하철_노선_수정(modifyRequest, "/lines/2");

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest request = new LineRequest("신분당선", "bg-red-600");
        지하철_노선_생성(request, "/lines");

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = 지하철_노선_제거("/lines/1");

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 지하철_노선_생성(LineRequest params, String path) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(path)
            .then().log().all()
            .extract();

        return response;
    }

    private ExtractableResponse<Response> 지하철_노선_조회(String path) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get(path)
            .then().log().all()
            .extract();

        return response;
    }

    private ExtractableResponse<Response> 지하철_노선_수정(LineRequest params, String path) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(path)
            .then().log().all()
            .extract();

        return response;
    }

    private ExtractableResponse<Response> 지하철_노선_제거(String path) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete(path)
            .then().log().all()
            .extract();

        return response;
    }
}
