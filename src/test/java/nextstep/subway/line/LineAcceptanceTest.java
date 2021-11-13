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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        //given
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = createLine("1호선", "blue", "청량리역", "영등포역", 10);

        // then
        // 지하철_노선_생성됨
        checkCreatedLine(response);
    }


    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        createLine("1호선", "blue", 1L, 2L, 10);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = createLine("1호선", "blue", 1L, 2L, 10);
        // then
        // 지하철_노선_생성_실패됨
        failCreatedLine(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        createLine("1호선", "blue", "청량리역", "영등포역", 10);
        createLine("2호선", "green", "당산역", "한양대역", 10);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = selectAllLines();
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        createLine("1호선", "blue", "청량리역", "영등포역", 10);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = selectOneLine("1");
        // then
        // 지하철_노선_응답됨
        isLineOKResponse(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse1 = createLine("1호선", "blue", "청량리역", "영등포역", 10);
        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = requestModifyLine(createResponse1, "1", "2호선", "green", "홍대입구역", "이대역", 20);
        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        createLine("1호선", "blue", "청량리역", "영등포역", 10);
        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = removeLine("1");

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

    }

    private ExtractableResponse<Response> createLine(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createLine(String name, String color, Long upStationId, Long downStationId, int distance) {
        LineRequest request = createLineRequest(name, color, upStationId, downStationId, distance);

        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createLine(String name, String color, String upStationName, String downStationName, int distance) {
        LineRequest request = createLineRequest(name, color, upStationName, downStationName, distance);

        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private LineRequest createLineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        return new LineRequest(name, color, upStationId, downStationId, distance);
    }

    private LineRequest createLineRequest(String name, String color, String upStationName, String downStationName, int distance) {
        Long upStationId = searchStationId(upStationName);
        Long downStationId = searchStationId(downStationName);
        return new LineRequest(name, color, upStationId, downStationId, distance);
    }

    private void checkCreatedLine(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotNull();
    }

    private void failCreatedLine(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void isLineOKResponse(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void checkLineList(ExtractableResponse<Response> createResponse1, ExtractableResponse<Response> createResponse2, ExtractableResponse<Response> response) {
        // 지하철_노선_목록_포함됨
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    private ExtractableResponse<Response> selectAllLines() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> selectOneLine(String lineNumber) {
        return RestAssured.given().log().all()
                .when()
                .get("/lines/" + lineNumber)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> requestModifyLine(ExtractableResponse<Response> createResponse1, String id, String name, String color, String upStationName, String downStationName, int distance) {
        LineRequest request = createLineRequest(name, color, upStationName, downStationName, distance);
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(createResponse1.header("Location"))
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> removeLine(String lineNumber) {
        return RestAssured.given().log().all()
                .when()
                .delete("/lines/" + lineNumber)
                .then().log().all()
                .extract();
    }

    public ExtractableResponse<Response> createStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        return response;
    }

    private Long searchStationId(String name) {
        ExtractableResponse<Response> createResponse = createStation(name);
        return Long.parseLong(createResponse.header("Location").split("/")[2]);
    }
}
