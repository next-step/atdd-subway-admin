package nextstep.subway.line.acceptance;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@DisplayName("지하철 노선 인수 테스트")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("2호선", "green");

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        LineResponse 이호선 = 지하철_노선_등록되어_있음("2호선", "green");

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(이호선.getName(), "red");

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        LineResponse 이호선 = 지하철_노선_등록되어_있음("2호선", "green");
        LineResponse 사호선 = 지하철_노선_등록되어_있음("4호선", "blue");

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(response, Arrays.asList(이호선, 사호선));
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        LineResponse 이호선 = 지하철_노선_등록되어_있음("2호선", "green");

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(이호선);

        // then
        지하철_노선_응답됨(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        LineResponse 이호선 = 지하철_노선_등록되어_있음("2호선", "green");

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(이호선.getId(), "4호선", "blue");

        // then
        지하철_노선_수정됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        LineResponse 이호선 = 지하철_노선_등록되어_있음("2호선", "green");

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(이호선.getId());

        // then
        지하철_노선_삭제됨(response);
    }

    private LineResponse 지하철_노선_등록되어_있음(String name, String color) {
        return 지하철_노선_생성_요청(name, color).as(LineResponse.class);
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(LineResponse 이호선) {
        return RestAssured
                .given().log().all()
                .when().get("/lines/{id}", 이호선.getId())
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(Long id, String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(Long id) {
        return RestAssured
                .given().log().all()
                .when().delete("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response, List<LineResponse> lineResponses) {
        List<Long> lineIds = new ArrayList<>(response.jsonPath().getList(".", LineResponse.class))
                .stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        List<Long> expectedPlaceIds = lineResponses.stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(lineIds).containsAll(expectedPlaceIds);
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
