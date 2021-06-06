package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {


    private static long upStationId;
    private static long upStationId2;
    private static long downStationId;
    private static long downStationId2;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        upStationId = 지하철_역_생성_후_ID_반환("강남");
        downStationId = 지하철_역_생성_후_ID_반환("광교");
        upStationId2 = 지하철_역_생성_후_ID_반환("도농");
        downStationId2 = 지하철_역_생성_후_ID_반환("양원");
    }



    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_등록("1호선", "빨강");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();

        List<Long> stations = response.body().jsonPath().getObject(".", LineResponse.class).getStations().stream()
                .map(Station::getId).collect(Collectors.toList());

        assertThat(stations).contains(upStationId, downStationId);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_등록("1호선", "빨강");

        // when
        ExtractableResponse<Response> response = 지하철_노선_등록("1호선", "빨강");

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> createResponse1 = 지하철_노선_등록("1호선", "빨강");
        ExtractableResponse<Response> createResponse2 = 지하철_노선_등록("2호선", "초록", upStationId2, downStationId2,50);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(res -> Long.parseLong(res.header("Location").split("/")[2]))
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
        ExtractableResponse<Response> createResponse = 지하철_노선_등록("1호선", "빨강");
        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createResponse);

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> resultLineIds = response.body().jsonPath().getObject(".", LineResponse.class).getStations().stream()
                .map(Station::getId).collect(Collectors.toList());

        assertThat(resultLineIds).containsExactly(upStationId, downStationId);

    }

    private ExtractableResponse<Response> 지하철_노선_조회_실패_요청(ExtractableResponse<Response> createResponse) {
        return given()
                .log().all()
                .when()
                .get("/lines/line/" + -1)
                .then()
                .log().all().extract();
    }

    @DisplayName("지하철 노선을 조회하지 못한다.")
    @Test
    void NotFoundGetLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = 지하철_노선_등록("1호선", "빨강");
        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회_실패_요청(createResponse);

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse =
                지하철_노선_등록("2호선", "ga-100", upStationId, downStationId, 100);
        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(createResponse);
        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> resultLineIds = response.body().jsonPath().getObject(".", LineResponse.class).getStations().stream()
                .map(Station::getId).collect(Collectors.toList());
        assertThat(resultLineIds).contains(upStationId2, downStationId2);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = 지하철_노선_등록("1호선", "빨강");

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(createResponse);

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given()
                .log().all()
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_등록(String 이름, String 색깔) {
        return 지하철_노선_등록(이름, 색깔, upStationId, downStationId, 50);
    }

    private ExtractableResponse<Response> 지하철_노선_등록(String 이름, String 색깔, Long 상행종착역ID, Long 하행종착역ID, int 거리) {
        Map<String, Object> originLine = new HashMap<>();
        originLine.put("name", 이름);
        originLine.put("color", 색깔);
        originLine.put("upStationId", 상행종착역ID);
        originLine.put("downStationId", 하행종착역ID);
        originLine.put("distance", 거리);

        return given().log().all()
                .when()
                .body(originLine)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post("/lines")
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(ExtractableResponse<Response> createResponse) {
        String linesId = createResponse.header("Location").split("/")[2];
        Map<String, Object> params = new HashMap<>();
        params.put("name", "10호선");
        params.put("color", "보라색");
        params.put("upStationId", upStationId2);
        params.put("downStationId", downStationId2);
        params.put("distance", 100);
        return given()
                .log().all()
                .when()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .put("/lines/line/" + linesId)
                .then()
                .log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(ExtractableResponse<Response> createResponse) {
        String location = createResponse.header("Location").split("/")[2];
        return given()
                .log().all()
                .when()
                .get("/lines/line/" + location)
                .then()
                .log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(ExtractableResponse<Response> createResponse) {
        String location = createResponse.header("Location");
        return RestAssured.given().log().all()
                .when()
                .delete(location)
                .then().log().all()
                .extract();
    }

    private static long 지하철_역_생성_후_ID_반환(String 이름) {
        Map<String, String> params = new HashMap<>();
        params.put("name", 이름);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        return Long.parseLong(response.header("Location").split("/")[2]);
    }
}
