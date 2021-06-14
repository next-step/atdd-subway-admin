package nextstep.subway.line;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given : 지하철_노선_정보
        Map<String, String> params = new HashMap<>();
        params.put("name", "1호선");
        params.put("color", "#0000FF");

        // when : 지하철_노선_생성_요청
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // then : 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = new HashMap<>();
        params.put("name", "1호선");
        params.put("color", "#0000FF");

        RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all();

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
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params01 = new HashMap<>();
        params01.put("name", "1호선");
        params01.put("color", "#0000FF");
        ExtractableResponse<Response> createResponse01 = RestAssured.given().log().all()
            .body(params01)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all().extract();

        // 지하철_노선_등록되어_있음
        Map<String, String> params02 = new HashMap<>();
        params02.put("name", "2호선");
        params02.put("color", "#008000");
        ExtractableResponse<Response> createResponse02 = RestAssured.given().log().all()
            .body(params02)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all().extract();

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // 지하철_노선_목록_포함됨
        List<Long> expectedLineIds = Arrays.asList(createResponse01, createResponse02).stream()
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
        Map<String, String> params01 = new HashMap<>();
        params01.put("name", "1호선");
        params01.put("color", "#0000FF");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
            .body(params01)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all().extract();

        // when
        // 지하철_노선_조회_요청
        String expectLineId = createResponse.header("Location").split("/")[2];
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines/" + expectLineId)
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_응답됨
        assertEquals(expectLineId, response.jsonPath().get("id").toString());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params01 = new HashMap<>();
        params01.put("name", "1호선");
        params01.put("color", "#0000FF");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
            .body(params01)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all().extract();

        // when
        // 지하철_노선_수정_요청
        String updateLineId = createResponse.header("Location").split("/")[2];
        Map<String, String> params02 = new HashMap<>();
        params01.put("name", "1호선");
        params01.put("color", "#FF0000");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params02)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/" + updateLineId)
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_수정됨
        assertEquals(params02.get("color"), response.jsonPath().get("color"));
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params01 = new HashMap<>();
        params01.put("name", "1호선");
        params01.put("color", "#0000FF");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
            .body(params01)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all().extract();

        // when
        // 지하철_노선_제거_요청
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
