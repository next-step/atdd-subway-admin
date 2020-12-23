package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> params = lineParamsGenerator("bg-red-600", "신분당선");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

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
        Map<String, String> params = lineParamsGenerator("bg-red-600", "신분당선");
        createLine(params);
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .log().all()
                .extract();

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> 신분당선 = lineParamsGenerator("bg-red-600", "신분당선");
        ExtractableResponse<Response> 신분당선_응답 = createLine(신분당선);

        // 지하철_노선_등록되어_있음
        Map<String, String> 이호선 = lineParamsGenerator("bg-green-600", "2호선");
        ExtractableResponse<Response> 이호선_응답 = createLine(이호선);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(이호선)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        List<Long> expectedLineIds = Stream.of(신분당선_응답, 이호선_응답)
                .map(this::extractLocationByResponse)
                .collect(Collectors.toList());

        List<Long> resultLineIds = createResponse.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);

    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> 신분당선 = lineParamsGenerator("bg-red-600", "신분당선");
        ExtractableResponse<Response> 신분당선_응답 = createLine(신분당선);

        // when
        // 지하철_노선_조회_요청
        Long lineId = extractLocationByResponse(신분당선_응답);
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(신분당선)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/" + lineId)
                .then().log().all()
                .extract();

        // then
        // 지하철_노선_응답됨
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        // TODO : 응답 항목 조회
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> 신분당선 = lineParamsGenerator("bg-red-600", "신분당선");
        ExtractableResponse<Response> 신분당선_응답 = createLine(신분당선);

        // when
        // 지하철_노선_수정_요청
        Long lineId = extractLocationByResponse(신분당선_응답);
        Map<String, String> 수정될_신분당선 = lineParamsGenerator("bg-green-600", "2호선");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(수정될_신분당선)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + lineId)
                .then().log().all()
                .extract();

        // then
        // 지하철_노선_수정됨
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> 신분당선 = lineParamsGenerator("bg-red-600", "신분당선");
        ExtractableResponse<Response> 신분당선_응답 = createLine(신분당선);

        // when
        // 지하철_노선_제거_요청
        Long lineId = extractLocationByResponse(신분당선_응답);
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + lineId)
                .then().log().all()
                .extract();

        // then
        // 지하철_노선_삭제됨
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private Map<String, String> lineParamsGenerator(String color, String name) {
        Map<String, String> params = new HashMap<>();
        params.put("color", color);
        params.put("name", name);
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

    private Long extractLocationByResponse(ExtractableResponse<Response> response) {
        return Long.parseLong(response.header("Location").split("/")[2]);
    }


}
