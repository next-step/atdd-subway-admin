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
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = createSubwayLine("1호선", "파랑");
        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        createSubwayLine("1호선", "파랑");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = createSubwayLine("1호선", "파랑");

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse1 = createSubwayLine("1호선", "파랑");
        ExtractableResponse<Response> createResponse2 = createSubwayLine("2호선", "초록");

        // when
        // 지하철_노선_목록_조회_요청

        ExtractableResponse<Response> response = getSubwayLineList();

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);

    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = createSubwayLine("1호선", "파랑");
        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = getSubwayLine(createResponse);
        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = createSubwayLine("1호선", "파랑");
        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = updateSubwayLine(createResponse, "1호선", "초록");
        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
        assertThat(lineResponse.getColor()).isEqualTo("초록");
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = createSubwayLine("1호선", "파랑");
        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = deleteSubwayLine(createResponse);
        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

    }

    private ExtractableResponse<Response> createSubwayLine(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured.given()
                .log().all()
                .when()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> updateSubwayLine(ExtractableResponse<Response> createResponse, String name, String color) {
        String lineId = createResponse.header("Location").split("/")[2];

        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured.given()
                .log().all()
                .when()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .put("/lines/" + lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> getSubwayLineList() {

        return RestAssured.given()
                .log().all()
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .get("/lines")
                .then().log().all()
                .extract();

    }

    private ExtractableResponse<Response> getSubwayLine(ExtractableResponse<Response> createResponse) {

        String lineId = createResponse.header("Location").split("/")[2];

        return RestAssured.given()
                .log().all()
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .get("/lines/"+lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> deleteSubwayLine(ExtractableResponse<Response> createResponse) {

        String lineId = createResponse.header("Location").split("/")[2];

        return RestAssured.given()
                .log().all()
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .delete("/lines/"+lineId)
                .then().log().all()
                .extract();
    }
}
