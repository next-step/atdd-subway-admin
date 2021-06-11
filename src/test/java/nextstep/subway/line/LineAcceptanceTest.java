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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> params = generateLineParam("2호선","green");

        // when
        ExtractableResponse<Response> response = saveLine(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }


    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        Map<String, String> params = generateLineParam("2호선","green");
        saveLine(params);

        // when
        ExtractableResponse<Response> response = saveLine(params);

        // then
        // 생성 실패
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());

    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        Map<String, String> params = generateLineParam("2호선","green");
        saveLine(params);
        Map<String, String> newParams = generateLineParam("3호선","orange");
        saveLine(newParams);

        // when
        ExtractableResponse<Response> response = searchLines();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<String> names = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(lineResponse -> lineResponse.getName())
                .collect(Collectors.toList());
        assertThat(names).contains("2호선");
        assertThat(names).contains("3호선");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        Map<String, String> params = generateLineParam("2호선","green");
        ExtractableResponse<Response> expect = saveLine(params);
        LineResponse savedLine = expect.jsonPath().getObject(".", LineResponse.class);

        // when
        ExtractableResponse<Response> response = searchLine(savedLine.getId());
        LineResponse searchedLine = response.jsonPath().getObject(".", LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(savedLine.getId()).isEqualTo(searchedLine.getId());
        assertThat(savedLine.getName()).isEqualTo(searchedLine.getName());
    }

    @DisplayName("지하철 노선 조회 실패")
    @Test
    void getLineWithWrongId() {
        // given
        Map<String, String> params = generateLineParam("2호선","green");
        saveLine(params);

        // when
        ExtractableResponse<Response> response = searchLine(0L);

        // then
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
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


    private Map<String, String> generateLineParam(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return params;
    }

    private ExtractableResponse<Response> saveLine(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> searchLines() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> searchLine(long lineName) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/"+lineName)
                .then().log().all()
                .extract();
    }

}
