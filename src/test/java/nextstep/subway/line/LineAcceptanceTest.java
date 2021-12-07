package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ResponseBodyExtractionOptions;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final int DISTANCE = 10;

    private ExtractableResponse<Response> 지하철역_생성_요청(Map<String, String> params) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
    }

    private Long 지하철역_생성_요청(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);
        ExtractableResponse<Response> response = 지하철역_생성_요청(params);
        StationResponse stationResponse = response.jsonPath().getObject(".", StationResponse.class);
        return stationResponse.getId();
    }

    private ExtractableResponse<Response> 지하철_노선_등록되어_있음_1호선() {
        Long 청량리역_ID = 지하철역_생성_요청("청량리역");
        Long 서울역_ID = 지하철역_생성_요청("서울역");
        return 지하철_노선_생성_요청("1호선", "blue", 청량리역_ID, 서울역_ID, DISTANCE);
    }

    private ExtractableResponse<Response> 지하철_노선_등록되어_있음_2호선() {
        Long 신촌역_ID = 지하철역_생성_요청("신촌역");
        Long 강남역_ID = 지하철역_생성_요청("강남역");
        return 지하철_노선_생성_요청("2호선", "green", 신촌역_ID, 강남역_ID, DISTANCE);
    }

    public static ResponseBodyExtractionOptions 지하철_노선_등록되어_있음(String name, String color,
        Long upStationId, Long downStationId, int distance) {
        return 지하철_노선_생성_요청(name, color, upStationId, downStationId, distance);
    }

    private static ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color,
        Long upStationId, Long downStationId, int distance) {
        Map<String, String> requestParam = new HashMap<>();
        requestParam.put("name", name);
        requestParam.put("color", color);
        requestParam.put("upStationId", upStationId.toString());
        requestParam.put("downStationId", downStationId.toString());
        requestParam.put("distance", distance + "");
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestParam)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(String uri) {
        return RestAssured
            .given().log().all()
            .when()
            .get(uri)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
            .given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(String uri, LineRequest request) {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .when()
            .put(uri)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(String uri) {
        return RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();
    }

    private void 응답_코드_검증(ExtractableResponse<Response> response, HttpStatus status) {
        assertThat(response.statusCode()).isEqualTo(status.value());
    }

    private void 지하철_노선_응답에_정류장_포함됨(LineResponse lineResponse) {
        assertThat(lineResponse.getStations()).isNotNull();
        assertThat(lineResponse.getStations()).isNotEmpty();
    }

    private void 정류장_목록이_상행선부터_하행선_순으로_정렬됨(List<String> expectedStations,
        ExtractableResponse<Response> response) {
        List<String> actualStations = response.jsonPath().getObject(".", LineResponse.class)
            .getStations().stream()
            .map(Station::getName)
            .collect(Collectors.toList());

        assertThat(expectedStations).containsExactlyElementsOf(actualStations);
    }

    private void 지하철_노선_목록_포함됨(List<ExtractableResponse<Response>> expectedList,
        ExtractableResponse<Response> actual) {
        List<Long> expectedLineIds = expectedList.stream()
            .map(createResponse ->
                Long.parseLong(createResponse
                    .header("Location")
                    .split("/")[2]))
            .collect(Collectors.toList());
        List<Long> actualLineIds = actual.jsonPath()
            .getList(".", LineResponse.class)
            .stream()
            .peek(this::지하철_노선_응답에_정류장_포함됨)
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        assertThat(actualLineIds).containsAll(expectedLineIds);
    }

    private void 지하철_노선_조회됨(String expectedName, String expectedColor, List<String> expectedStations,
        ExtractableResponse<Response> response) {
        지하철_노선_일치함(expectedName, expectedColor, response);
        지하철_노선_응답에_정류장_포함됨(response.jsonPath()
            .getObject(".", LineResponse.class));
        정류장_목록이_상행선부터_하행선_순으로_정렬됨(expectedStations, response);
    }

    private void 지하철_노선_수정됨(String expectedName, String expectedColor, ExtractableResponse<Response> response) {
        지하철_노선_일치함(expectedName, expectedColor, response);
    }

    private void 지하철_노선_일치함(String expectedName, String expectedColor, ExtractableResponse<Response> response) {
        LineResponse actual = response.jsonPath().getObject(".", LineResponse.class);
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo(expectedName);
        assertThat(actual.getColor()).isEqualTo(expectedColor);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Long 청량리역_ID = 지하철역_생성_요청("청량리역");
        Long 서울역_ID = 지하철역_생성_요청("서울역");

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("1호선", "blue", 청량리역_ID, 서울역_ID, DISTANCE);

        // then
        응답_코드_검증(response, HttpStatus.CREATED);
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_등록되어_있음_1호선();
        Long 신촌역_ID = 지하철역_생성_요청("신촌역");
        Long 강남역_ID = 지하철역_생성_요청("강남역");

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("1호선", "yellow", 신촌역_ID, 강남역_ID, DISTANCE);

        // then
        응답_코드_검증(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("존재하지 않는 지하철 역을 포함한 지하철 노선을 생성한다.")
    @Test
    void createLine3() {
           // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("3호선", "orange", 100L, 101L, DISTANCE);

        // then
        응답_코드_검증(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> createResponse1 = 지하철_노선_등록되어_있음_1호선();
        ExtractableResponse<Response> createResponse2 = 지하철_노선_등록되어_있음_2호선();

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        응답_코드_검증(response, HttpStatus.OK);
        지하철_노선_목록_포함됨(Arrays.asList(createResponse1, createResponse2), response);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음_1호선();

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createResponse.header("Location"));

        // then
        응답_코드_검증(response, HttpStatus.OK);
        지하철_노선_조회됨("1호선", "blue", Arrays.asList("청량리역", "서울역"), response);
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회한다.")
    @Test
    void getLine_Fail() {

        // when
        String uri = "/lines/1";
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(uri);

        // then
        응답_코드_검증(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음_1호선();

        // when
        String uri = createResponse.header("Location");
        LineRequest lineRequest = new LineRequest("2호선", "green");
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(uri, lineRequest);

        // then
        응답_코드_검증(response, HttpStatus.OK);
        지하철_노선_수정됨("2호선", "green", response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음_1호선();

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
