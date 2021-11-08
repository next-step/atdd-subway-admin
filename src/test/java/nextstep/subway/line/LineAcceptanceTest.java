package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @ParameterizedTest
    @CsvSource(value = {"신분당선,RED", "분당선,YELLOW", "1호선,BLUE", "2호선,GREEN"})
    void createLine(String name, String color) {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(name, color);

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @ParameterizedTest
    @CsvSource(value = {"신분당선,RED", "분당선,YELLOW", "1호선,BLUE", "2호선,GREEN"})
    void createLine2(String name, String color) {
        // given
        지하철_노선_등록되어_있음(name, color);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(name, color);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @ParameterizedTest
    @CsvSource(value = {"신분당선,RED,분당선,YELLOW", "1호선,BLUE,2호선,GREEN"})
    void getLines(String name1, String color1, String name2, String color2) {
        // given
        LineResponse line1 = 지하철_노선_등록되어_있음(name1, color1);
        LineResponse line2 = 지하철_노선_등록되어_있음(name2, color2);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(response, Arrays.asList(line1, line2));
    }

    @DisplayName("지하철 노선을 조회한다.")
    @ParameterizedTest
    @CsvSource(value = {"신분당선,RED", "분당선,YELLOW", "1호선,BLUE", "2호선,GREEN"})
    void getLine(String name, String color) {
        // given
        LineResponse line = 지하철_노선_등록되어_있음(name, color);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(line.getId());

        // then
        지하철_노선_응답됨(response);
        지하철_노선_포함됨(response, line);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @ParameterizedTest
    @CsvSource(value = {"신분당선,RED,신분당선2,RED2", "분당선,YELLOW,분당선2,YELLOW2", "1호선,BLUE,1호선2,BLUE2"})
    void updateLine(String name, String color, String updateName, String updateColor) {
        // given
        LineResponse line = 지하철_노선_등록되어_있음(name, color);

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(line, updateName, updateColor);

        // then
        지하철_노선_수정됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @ParameterizedTest
    @CsvSource(value = {"신분당선,RED", "분당선,YELLOW", "1호선,BLUE", "2호선,GREEN"})
    void deleteLine(String name, String color) {
        // given
        LineResponse line = 지하철_노선_등록되어_있음(name, color);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(line);

        // then
        지하철_노선_삭제됨(response);
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(LineResponse line) {
        return RestAssured.given().log().all()
                          .when()
                          .delete("/lines/{id}", line.getId())
                          .then().log().all()
                          .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(LineResponse line, String updateName, String updateColor) {
        Map<String, String> params = createLineParams(updateName, updateColor);

        return RestAssured.given().log().all()
                          .body(params)
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when()
                          .put("/lines/{id}", line.getId())
                          .then().log().all()
                          .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
        return RestAssured.given().log().all()
                          .when()
                          .get("/lines/{id}", id)
                          .then().log().all()
                          .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given().log().all()
                          .when()
                          .get("/lines")
                          .then().log().all()
                          .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color) {
        Map<String, String> params = createLineParams(name, color);

        return RestAssured.given().log().all()
                          .body(params)
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when()
                          .post("/lines")
                          .then().log().all()
                          .extract();
    }

    private LineResponse 지하철_노선_등록되어_있음(String name, String color) {
        Map<String, String> params = createLineParams(name, color);

        return RestAssured.given().log().all()
                          .body(params)
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when()
                          .post("/lines")
                          .then().log().all()
                          .extract()
                          .as(LineResponse.class);
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_포함됨(ExtractableResponse<Response> response, LineResponse line) {
        LineResponse resultLine = response.jsonPath()
                                          .getObject(".", LineResponse.class);
        assertThat(resultLine.getId()).isEqualTo(line.getId());
    }

    private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response, List<LineResponse> expectedLines) {
        List<Long> expectedLineIds = expectedLines.stream()
                                                  .map(LineResponse::getId)
                                                  .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath()
                                           .getList(".", LineResponse.class)
                                           .stream()
                                           .map(LineResponse::getId)
                                           .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private Map<String, String> createLineParams(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return params;
    }
}
