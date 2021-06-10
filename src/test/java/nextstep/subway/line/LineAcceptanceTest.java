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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private static final String FIRST_LINE_NAME = "1호선";
    private static final String SECOND_LINE_NAME = "2호선";
    private static final String BLACK_LINE_COLOR = "black";
    private static final String RED_LINE_COLOR = "red";

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        //given && when 지하철_노선_생성
        ExtractableResponse<Response> response = requestCreateLine(FIRST_LINE_NAME, BLACK_LINE_COLOR);
        // then 지하철_노선_생성됨
        assertHttpStatus(response, HttpStatus.CREATED);
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplication() {
        // given 지하철_노선_등록되어_있음
        requestCreateLine(FIRST_LINE_NAME, BLACK_LINE_COLOR);
        // when 지하철_동일이름_노선_생성_요청
        ExtractableResponse<Response> response = requestCreateLine(FIRST_LINE_NAME, BLACK_LINE_COLOR);
        // then 지하철_노선_생성_실패됨
        assertHttpStatus(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given 지하철_노선_2개_등록되어_있음
        ExtractableResponse<Response> createdResponse1 = requestCreateLine(FIRST_LINE_NAME, BLACK_LINE_COLOR);
        ExtractableResponse<Response> createdResponse2 = requestCreateLine(SECOND_LINE_NAME, RED_LINE_COLOR);
        // when 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all().extract();
        // then 지하철_노선_목록_응답됨
        assertHttpStatus(response, HttpStatus.OK);
        // 지하철_노선_목록_포함됨
        List<Long> expectedLineIds = Stream.of(createdResponse1, createdResponse2)
                .map(expectedResponse -> expectedResponse.jsonPath().getObject(".", LineResponse.class).getId())
                .collect(Collectors.toList());
        List<Long> actualLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(actualLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given 지하철_노선_등록되어_있음
        requestCreateLine(FIRST_LINE_NAME, BLACK_LINE_COLOR);
        // when 지하철_노선_조회_요청
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/lines/1")
                .then().log().all().extract();
        // then 지하철_노선_응답됨
        assertHttpStatus(response, HttpStatus.OK);
        assertThat(response.jsonPath().getObject(".", LineResponse.class)).isNotNull();
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given 지하철_노선_등록되어_있음
        requestCreateLine(FIRST_LINE_NAME, BLACK_LINE_COLOR);
        // when 지하철_노선_수정_요청
        ExtractableResponse<Response> response = requestUpdateLine(makeParams(FIRST_LINE_NAME, RED_LINE_COLOR), 1L);
        // then 지하철_노선_수정됨
        String actualColor = response.jsonPath().get("color").toString();
        assertThat(actualColor).isEqualTo(RED_LINE_COLOR);
    }

    @DisplayName("존재하지 않는 지하철 노선을 수정한다.")
    @Test
    void updateNonExistentLine() {
        // given 지하철_노선_등록되어_있음
        requestCreateLine(FIRST_LINE_NAME, BLACK_LINE_COLOR);
        // when 지하철_노선_수정_요청
        ExtractableResponse<Response> response = requestUpdateLine(makeParams(FIRST_LINE_NAME, RED_LINE_COLOR), 2L);
        // then 지하철_노선_수정_실패됨
        assertHttpStatus(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given 지하철_노선_등록되어_있음
        requestCreateLine(FIRST_LINE_NAME, BLACK_LINE_COLOR);
        // when 지하철_노선_제거_요청
        ExtractableResponse<Response> response = requestDeleteLine(1L);
        // then 지하철_노선_삭제됨
        assertHttpStatus(response, HttpStatus.NO_CONTENT);
    }

    @DisplayName("존재하지 않는 지하철 노선을 제거한다.")
    @Test
    void deleteNonExistentLine() {
        // given 지하철_노선_등록되어_있음
        requestCreateLine(FIRST_LINE_NAME, BLACK_LINE_COLOR);
        // when 지하철_노선_제거_요청
        ExtractableResponse<Response> response = requestDeleteLine(2L);
        // then 지하철_노선_삭제됨
        assertHttpStatus(response, HttpStatus.BAD_REQUEST);
    }

    private void assertHttpStatus(ExtractableResponse<Response> response, HttpStatus status) {
        assertThat(response.statusCode()).isEqualTo(status.value());
    }

    private ExtractableResponse<Response> requestCreateLine(String name, String color) {
        Map<String, String> params = makeParams(name,color);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> requestUpdateLine(Map<String, String> params, Long id) {
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/" + id)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> requestDeleteLine(Long id) {
        return RestAssured
                .given().log().all()
                .when().delete("/lines/" + id)
                .then().log().all().extract();
    }

    private Map<String, String> makeParams(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return params;
    }
}
