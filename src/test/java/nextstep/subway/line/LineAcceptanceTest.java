package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
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
        Map<String, String> params = getRequestParam("2호선", "green");

        // when
        ExtractableResponse<Response> response = createLine(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicatedName() {
        // given
        Map<String, String> params = getRequestParam("2호선", "green");
        createLine(params);

        // when
        ExtractableResponse<Response> response = createLine(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // param1 등록
        Map<String, String> params1 = getRequestParam("2호선", "green");
        ExtractableResponse<Response> createResponse1 = createLine(params1);

        // param2 등록
        Map<String, String> params2 = getRequestParam("5호선", "purple");
        ExtractableResponse<Response> createResponse2 = createLine(params2);

        // when
        // 조회
        ExtractableResponse<Response> response = findAll();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(it -> it.getId())
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        String expectedName = "2호선";
        String expectedColor = "green";
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = getRequestParam(expectedName, expectedColor);
        ExtractableResponse<Response> createResponse = createLine(params);

        String id = createResponse.header("Location").split("/")[2];

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = findById(id);

        // then
        // 지하철_노선_응답됨
        String actualName = response.body().jsonPath().get("name");
        String actualColor = response.body().jsonPath().get("color");
        assertThat(expectedName).isEqualTo(actualName);
        assertThat(expectedColor).isEqualTo(actualColor);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        String expectedName = "5호선";
        String expectedColor = "purple";
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> createParams = getRequestParam("2호선", "green");
        ExtractableResponse<Response> createResponse = createLine(createParams);

        String id = createResponse.header("Location").split("/")[2];

        // when
        // 지하철_노선_수정_요청
        Map<String, String> updateParams = getRequestParam(expectedName, expectedColor);
        updateLine(id, updateParams);

        // then
        // 지하철_노선_수정됨
        ExtractableResponse<Response> response = findById(id);

        String actualName = response.body().jsonPath().get("name");
        String actualColor = response.body().jsonPath().get("color");
        assertThat(expectedName).isEqualTo(actualName);
        assertThat(expectedColor).isEqualTo(actualColor);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> createParams = getRequestParam("2호선", "green");
        ExtractableResponse<Response> createResponse = createLine(createParams);

        String id = createResponse.header("Location").split("/")[2];

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = deleteLine(id);

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private Map<String, String> getRequestParam(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return params;
    }

    private ExtractableResponse<Response> createLine(Map<String, String> params) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> findAll() {
        return RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> findById(String id) {
        return RestAssured.given().log().all()
            .when()
            .get("/lines/" + id)
            .then().log().all()
            .extract();
    }

    private void updateLine(String id, Map<String, String> updateParams) {
        RestAssured.given().log().all()
            .body(updateParams)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/" + id)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> deleteLine(String id) {
        return RestAssured.given().log().all()
            .when()
            .delete("/lines/" + id)
            .then().log().all()
            .extract();
    }
}
