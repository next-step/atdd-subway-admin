package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final String LINE_DEFAULT_URI = "/lines";
    private static final String LINE_ID_URI = "/lines/1";

    @BeforeEach
    void setting() {
        StationAcceptanceTest.지하철_역_생성(StationAcceptanceTest.지하철_역_제공("강남역"));
        StationAcceptanceTest.지하철_역_생성(StationAcceptanceTest.지하철_역_제공("역삼역"));
        StationAcceptanceTest.지하철_역_생성(StationAcceptanceTest.지하철_역_제공("교대역"));
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        //given
        Map<String, String> params = 지하철_노선_종점_제공("2호선", "green darken-1", "1", "2", "10");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성(params);

        // then
        // 지하철_노선_생성됨
        지하철_노선_생성_성공(response);
        지하철_노선_일치_성공(response, "2호선");
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = 지하철_노선_종점_제공("2호선", "green darken-1", "1", "2", "10");
        지하철_노선_생성(params);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성(params);

        // then
        // 지하철_노선_생성_실패됨
        지하철_노선_생성_실패(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params1 = 지하철_노선_종점_제공("2호선", "green darken-1", "1", "2", "10");
        ExtractableResponse<Response> createResponse1 = 지하철_노선_생성(params1);

        // 지하철_노선_등록되어_있음
        Map<String, String> params2 = 지하철_노선_종점_제공("신분당선", "red darken-1", "2", "3", "10");
        ExtractableResponse<Response> createResponse2 = 지하철_노선_생성(params2);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회(LINE_DEFAULT_URI);

        // then
        // 지하철_노선_목록_응답됨
        지하철_노선_응답_성공(response);
        List<Long> expectedLineIds = 지하철_노선_목록_예상(new ArrayList<>(Arrays.asList(createResponse1, createResponse2)));
        List<Long> resultLineIds = 지하철_노선_목록_응답(response);
        // 지하철_노선_목록_포함됨
        지하철_노선_목록_일치_성공(resultLineIds, expectedLineIds);

    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = 지하철_노선_종점_제공("2호선", "green darken-1", "1", "2", "10");
        지하철_노선_생성(params);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회(LINE_ID_URI);

        // then
        // 지하철_노선_응답됨
        지하철_노선_응답_성공(response);
        지하철_노선_일치_성공(response, "2호선");
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = 지하철_노선_종점_제공("2호선", "green darken-1", "1", "2", "10");
        지하철_노선_생성(params);

        // when
        // 지하철_노선_수정_요청
        //params.get("name")
        Map<String, String> updateParams = 지하철_노선_제공("신분당선", "green darken-1");
        ExtractableResponse<Response> updateResponse = 지하철_노선_수정(LINE_ID_URI, updateParams);

        // then
        // 지하철_노선_수정됨
        지하철_노선_응답_성공(updateResponse);
        지하철_노선_일치_성공(updateResponse, "신분당선");
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = 지하철_노선_종점_제공("2호선", "green darken-1", "1", "2", "10");
        지하철_노선_생성(params);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = 지하철_노선_삭제(LINE_ID_URI);

        // then
        // 지하철_노선_삭제됨
        지하철_노선_삭제_성공(response);
    }


    static Map<String, String> 지하철_노선_제공(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return params;
    }

    public static Map<String, String> 지하철_노선_종점_제공(String name, String color, String upStationId, String downStationId, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return params;
    }

    public static ExtractableResponse<Response> 지하철_노선_생성(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(LINE_DEFAULT_URI)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 지하철_노선_조회(String uri) {
        return RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 지하철_노선_수정(String uri, Map<String, String> updateParams) {
        return RestAssured.given().log().all()
                .body(updateParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .patch(uri)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 지하철_노선_삭제(String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }

    public static void 지하철_노선_일치_성공(ExtractableResponse<Response> response, String name) {
        assertThat(response.jsonPath().get("name").toString()).isEqualTo(name);
    }

    public static void 지하철_노선_생성_성공(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 지하철_노선_생성_실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.statusCode()).isNotEqualTo(HttpStatus.CREATED.value());
    }

    static void 지하철_노선_응답_성공(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    static List<Long> 지하철_노선_목록_예상(ArrayList<ExtractableResponse<Response>> responses) {
        return responses.stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
    }

    static List<Long> 지하철_노선_목록_응답(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
    }

    static void 지하철_노선_목록_일치_성공(List<Long> resultLineIds, List<Long> expectedLineIds) {
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    static void 지하철_노선_삭제_성공(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
