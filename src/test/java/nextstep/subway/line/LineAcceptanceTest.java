package nextstep.subway.line;

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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {


    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("신분당선", "bg-red-600",1L,2L,10);

        // then
        지하철_노선_생성됨(response);
    }
    // ------

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {

        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음("신분당선", "bg-red-600");

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("신분당선", "bg-red-600",1L,2L,10);

        // then
        지하철_노선_생성_실패됨(response);
    }


    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        지하철_노선_등록되어_있음("신분당선", "bg-red-600");
        지하철_노선_등록되어_있음("2호선", "bg-green-600");

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();


        // then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(response);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {

        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음("신분당선", "bg-red-600");

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createResponse);

        // then
        지하철_노선_응답됨(response);

    }


    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {

        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음("신분당선", "bg-red-600");

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청("구분당선", "bg-blue-600", createResponse);

        // then
        지하철_노선_수정됨(response);

    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(String name, String color, ExtractableResponse<Response> createResponse) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        String uri = createResponse.header("Location");
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all().extract();
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {

        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음("신분당선", "bg-red-600");

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청("구분당선", "bg-blue-600", createResponse);

        // then
        지하철_노선_삭제됨(response);

    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(String name, String color, ExtractableResponse<Response> createResponse) {
        String uri = createResponse.header("Location");

        return RestAssured.given().log().all().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                delete(uri)
                .then()
                .log().all()
                .extract();
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response) {
        List<LineResponse> list = response.jsonPath().getList(".", LineResponse.class);
        assertThat(list.size()).isEqualTo(2);
    }

    private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    /*
        private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;
     */
    private ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        params.put("distance", "10");

        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all().extract();
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(response.as(LineResponse.class)).isNotNull();
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(ExtractableResponse<Response> createResponse) {

        String uri = createResponse.header("Location");

        return RestAssured.given().log().all().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                get(uri)
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_등록되어_있음(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("startTime", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE));
        params.put("endTime", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE));
        params.put("intervalTime", "5");

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }
}
