package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static java.lang.Long.*;
import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.MediaType.*;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> params = 지하철_노선("수인분당선", "YELLOW");

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(params, APPLICATION_JSON_VALUE, "/lines");

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_생성_요청(지하철_노선("수인분당선", "YELLOW"), APPLICATION_JSON_VALUE, "/lines");

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(지하철_노선("수인분당선", "YELLOW"), APPLICATION_JSON_VALUE, "/lines");

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> created1 = 지하철_노선_생성_요청(지하철_노선("수인분당선", "YELLOW"), APPLICATION_JSON_VALUE, "/lines");
        ExtractableResponse<Response> created2 = 지하철_노선_생성_요청(지하철_노선("신분당선", "RED"), APPLICATION_JSON_VALUE, "/lines");
        ExtractableResponse<Response> created3 = 지하철_노선_생성_요청(지하철_노선("2호선", "GREEN"), APPLICATION_JSON_VALUE, "/lines");
        ExtractableResponse<Response> created4 = 지하철_노선_생성_요청(지하철_노선("3호선", "ORANGE"), APPLICATION_JSON_VALUE, "/lines");

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회("/lines");

        // then
        정상_OK_응답됨(response);
        생성된_지하철_노선_목록_응답된_지하철_노선_목록_검증(response, asList(created1, created2, created3, created4), "$");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> created1 = 지하철_노선_생성_요청(지하철_노선("2호선", "GREEN"), APPLICATION_JSON_VALUE, "/lines");
        ExtractableResponse<Response> created2 = 지하철_노선_생성_요청(지하철_노선("3호선", "ORANGE"), APPLICATION_JSON_VALUE, "/lines");

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회("/lines/2호선");

        // then
        정상_OK_응답됨(response);
        생성된_지하철_노선_목록_응답된_지하철_노선_검증(response, asList(created1, created2), "$");
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> created = 지하철_노선_생성_요청(지하철_노선("2호선", "GREEN"), APPLICATION_JSON_VALUE, "/lines");
        String id = String.valueOf(생성된_지하철_노선_목록(asList(created)).get(0));
        Map<String, String> params = 지하철_노선("3호선", "ORANGE");
        params.put("id", id);

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(params, APPLICATION_JSON_VALUE, "/lines/" + id);

        // then
        정상_OK_응답됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> created = 지하철_노선_생성_요청(지하철_노선("3호선", "ORANGE"), APPLICATION_JSON_VALUE, "/lines");
        String id = String.valueOf(생성된_지하철_노선_목록(asList(created)).get(0));

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청("/lines/" + id);

        // then
        정상_OK_응답됨(response);
    }

    public static Map<String, String> 지하철_노선(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return params;
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, String> params, String mediaTypeValue, String path) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(mediaTypeValue)
                .when()
                .post(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회(String path) {
        return RestAssured
                .given().log().all()
                .when()
                .get(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회(String path) {
        return 지하철_노선_목록_조회(path);
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(Map<String, String> params, String mediaTypeValue, String path) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(mediaTypeValue)
                .when()
                .put(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(String path) {
        return RestAssured.given().log().all()
                .when()
                .delete(path)
                .then().log().all()
                .extract();
    }

    public static void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 정상_OK_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 생성된_지하철_노선_목록_응답된_지하철_노선_목록_검증(ExtractableResponse<Response> response,
                                                      List<ExtractableResponse<Response>> createdResponses,
                                                      String jsonPathOperator) {
        List<Long> expectedLineIds = 생성된_지하철_노선_목록(createdResponses);
        List<Long> resultLineIds = response.jsonPath()
                .getList(jsonPathOperator, LineResponse.class)
                .stream()
                .map(LineResponse::getId)
                .collect(toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    public static void 생성된_지하철_노선_목록_응답된_지하철_노선_검증(ExtractableResponse<Response> response,
                                                      List<ExtractableResponse<Response>> createdResponses,
                                                      String jsonPathOperator) {
        List<Long> expectedLineIds = 생성된_지하철_노선_목록(createdResponses);
        LineResponse lineResponse = response.jsonPath()
                .getObject(jsonPathOperator, LineResponse.class);
        assertThat(expectedLineIds).contains(lineResponse.getId());
    }

    public static List<Long> 생성된_지하철_노선_목록(List<ExtractableResponse<Response>> createdResponses) {
        return createdResponses.stream()
                .map(cr -> parseLong(cr.header("Location").split("/")[2]))
                .collect(toList());
    }
}
