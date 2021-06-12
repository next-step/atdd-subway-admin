package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesSubResponse;
import nextstep.subway.section.SectionAcceptanceTest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private static final String BASE_PATH = "/lines";

    public static String location;

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        //given
        StationResponse upStation = 역_생성("방화역");
        StationResponse downStation = 역_생성("하남검단산역");

        Map<String, String> params = 지하철_노선_파라미터("5호선", "purple",
                String.valueOf(upStation.getId()), String.valueOf(downStation.getId()), "1000");

        // when
        ExtractableResponse<Response> extractableResponse = 지하철_노선_생성_요청(params);

        // then
        지하철_노선_생성됨(extractableResponse);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        //given
        지하철_노선_생성("5호선", "purple", "방화역", "하남검단산역", 1000);

        Map<String, String> params = 지하철_노선_파라미터("5호선", "purple",
                "1", "2", "1000");

        // when
        ExtractableResponse<Response> extractableResponse = 지하철_노선_생성_요청(params);

        // then
        지하철_노선_이름충돌로_생성되지_않음(extractableResponse);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        //given
        LineResponse line1 = 지하철_노선_생성("5호선", "purple", "방화역", "하남검단산역", 1000);
        LineResponse line2 = 지하철_노선_생성("4호선", "sky", "당고개역", "오이도역", 1000);

        // when
        ExtractableResponse<Response> extractableResponse = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_조회됨(extractableResponse, line1, line2);
    }

    @DisplayName("지하철 노선 조회시, 노선의 역 목록을 상행 종점부터 하행 종점까지 정렬하여 조회한다.")
    @Test
    void getLine() {
        //given
        LineResponse lineResponse = 지하철_노선_생성("5호선", "purple", "방화역", "하남검단산역", 1000);

        // when
        ExtractableResponse<Response> extractableResponse = 지하철_노선_조회_요청();

        // then
        지하철_노선_조회됨(extractableResponse, lineResponse);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        지하철_노선_생성("5호선", "purple", "방화역", "하남검단산역", 1000);

        // when
        Map<String, String> params = 지하철_노선_수정_파라미터("4호선", "sky");
        ExtractableResponse<Response> extractableResponse = 지하철_노선_수정_요청(params);

        // then
        지하철_노선_수정됨(extractableResponse, params);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        지하철_노선_생성("5호선", "purple", "방화역", "하남검단산역", 1000);

        // when
        ExtractableResponse<Response> extractableResponse = 지하철_노선_삭제_요청();

        // then
        지하철_노선_삭제됨(extractableResponse);
    }

    @DisplayName("구간등록 - " +
            "역 사이에 새로운 역을 등록할 경우 - " +
            "노선에 존재하는 역 하나와 노선에 존재하지 않는 역 하나, 아직 생성되지않은 구간을 생성하여 노선에 등록한다" +
            "이미 존재하는 역이 상행역인 경우")
    @Test
    public void 구간등록_등록확인1() throws Exception {
        //given
        지하철_노선_생성("테스트노선", "테스트색", "상행종점역", "하행종점역", 1000);

        StationResponse stationResponse = 역_생성("추가될역");

        //when
        Map<String, String> params = 구간_역_생성_및_노선등록_파라미터(1L, stationResponse.getId(), 500);
        ExtractableResponse<Response> extractableResponse = 구간_역_생성_및_노선등록_요청(params);

        //then
        구간_역_생성_및_노선등록됨(extractableResponse);
    }

    @DisplayName("구간등록 - " +
            "역 사이에 새로운 역을 등록할 경우 - " +
            "노선에 존재하는 역 하나와 노선에 존재하지 않는 역 하나, 아직 생성되지않은 구간을 생성하여 노선에 등록한다" +
            "이미 존재하는 역이 하행역인 경우")
    @Test
    public void 구간등록_등록확인5() throws Exception {
        //given
        지하철_노선_생성("테스트노선", "테스트색", "상행종점역", "하행종점역", 1000);

        StationResponse stationResponse = 역_생성("추가될역");

        //when
        Map<String, String> params = 구간_역_생성_및_노선등록_파라미터(stationResponse.getId(), 2L, 500);
        ExtractableResponse<Response> extractableResponse = 구간_역_생성_및_노선등록_요청(params);

        //then
        구간_역_생성_및_노선등록됨(extractableResponse);
    }

    @DisplayName("구간등록 - " +
            "노선에 존재하는 역 하나와 노선에 존재하지 않는 역 하나, 아직 생성되지않은 구간을 생성하여 노선에 등록한다" +
            "이미 존재하는 역이 상행종점역이고 새로 등록하는 역이 상행종점역이 되는 경우")
    @Test
    public void 구간등록_등록확인6() throws Exception {
        //given
        지하철_노선_생성("테스트노선", "테스트색", "상행종점역", "하행종점역", 1000);

        StationResponse stationResponse = 역_생성("추가될역");

        //when
        Map<String, String> params = 구간_역_생성_및_노선등록_파라미터(stationResponse.getId(), 1L, 500);
        ExtractableResponse<Response> extractableResponse = 구간_역_생성_및_노선등록_요청(params);

        //then
        구간_역_생성_및_노선등록됨(extractableResponse);
    }

    @DisplayName("구간등록 - " +
            "노선에 존재하는 역 하나와 노선에 존재하지 않는 역 하나, 아직 생성되지않은 구간을 생성하여 노선에 등록한다" +
            "이미 존재하는 역이 하행종점역이고 새로 등록하는 역이 하행종점역이 되는 경우")
    @Test
    public void 구간등록_등록확인7() throws Exception {
        //given
        지하철_노선_생성("테스트노선", "테스트색", "상행종점역", "하행종점역", 1000);

        StationResponse stationResponse = 역_생성("추가될역");

        //when
        Map<String, String> params = 구간_역_생성_및_노선등록_파라미터(2L, stationResponse.getId(), 500);
        ExtractableResponse<Response> extractableResponse = 구간_역_생성_및_노선등록_요청(params);

        //then
        구간_역_생성_및_노선등록됨(extractableResponse);
    }

    @DisplayName("구간등록 - 등록하려는 역이 모두 노선에 등록되어있지 않는 경우 등록할수 없다.")
    @Test
    public void 구간등록_등록확인2() throws Exception {
        //given
        지하철_노선_생성("테스트노선", "테스트색", "상행종점역", "하행종점역", 1000);

        StationResponse stationResponse1 = 역_생성("추가될역1");
        StationResponse stationResponse2 = 역_생성("추가될역2");

        //when
        Map<String, String> params = 구간_역_생성_및_노선등록_파라미터(stationResponse1.getId(), stationResponse2.getId(), 500);
        ExtractableResponse<Response> extractableResponse = 구간_역_생성_및_노선등록_요청(params);

        //then
        구간_역_생성_및_노선등록안됨(extractableResponse);
    }

    @DisplayName("구간등록 - 등록하려는 역이 모두 노선에 등록되어 있는 경우 등록할수 없다.")
    @Test
    public void 구간등록_등록확인3() throws Exception {
        //given
        지하철_노선_생성("테스트노선", "테스트색", "상행종점역", "하행종점역", 1000);

        //when
        Map<String, String> params = 구간_역_생성_및_노선등록_파라미터(1L, 2L, 500);
        ExtractableResponse<Response> extractableResponse = 구간_역_생성_및_노선등록_요청(params);

        //then
        구간_역_생성_및_노선등록안됨(extractableResponse);
    }

    @DisplayName("구간등록 - 등록하려는 구간의 길이가 기존에 존재하는 구간의 길이보다 크거나 같으면 등록할 수 없다. 또는 0, 음수여도")
    @ParameterizedTest
    @ValueSource(ints = {1000, 1001, 0, -1})
    public void 구간등록_등록확인4(int distance) throws Exception {
        //given
        지하철_노선_생성("테스트노선", "테스트색", "상행종점역", "하행종점역", 1000);

        StationResponse stationResponse = 역_생성("추가될역");

        //when
        Map<String, String> params = 구간_역_생성_및_노선등록_파라미터(1L, stationResponse.getId(), distance);
        ExtractableResponse<Response> extractableResponse = 구간_역_생성_및_노선등록_요청(params);

        //then
        구간_역_생성_및_노선등록안됨(extractableResponse);
    }
    
    @DisplayName("구간삭제 - 노선에 포함되어 있지 않은 역은 삭제할 수 없다.")
    @Test
    public void 구간삭제_예외발생1() throws Exception {
        //given
        지하철_노선_생성("테스트노선", "테스트색", "상행종점역", "하행종점역", 1000);
        StationResponse stationResponse = 역_생성("추가될역");

        // when
        ExtractableResponse<Response> response = 구간삭제_요청(stationResponse.getId());

        // then
        구간삭제안됨(response);
    }

    @DisplayName("구간삭제 - 구간이 하나만 존재할 때 역을 삭제할 수 없다.")
    @Test
    public void 구간삭제_예외발생2() throws Exception {
        //given
        지하철_노선_생성("테스트노선", "테스트색", "상행종점역", "하행종점역", 1000);
        LinesSubResponse linesSubResponse = 지하철_노선_조회();

        //when
        ExtractableResponse<Response> response = 구간삭제_요청(linesSubResponse.getStations().get(0).getId());

        //then
        구간삭제안됨(response);
    }

    @DisplayName("구간삭제 - 상행종점역을 삭제하는 경우")
    @Test
    public void 구간삭제_구간확인1() throws Exception {
        //given
        지하철_노선_생성("테스트노선", "테스트색", "상행종점역", "하행종점역", 1000);
        StationResponse stationResponse = 역_생성("추가될역");
        Map<String, String> params = 구간_역_생성_및_노선등록_파라미터(stationResponse.getId(), 1L, 500);
        ExtractableResponse<Response> extractableResponse = 구간_역_생성_및_노선등록_요청(params);
        구간_역_생성_및_노선등록됨(extractableResponse);

        //when
        ExtractableResponse<Response> response = 구간삭제_요청(stationResponse.getId());

        //then
        구간삭제됨(response);
    }

    @DisplayName("구간삭제 - 하행종점역을 삭제하는 경우")
    @Test
    public void 구간삭제_구간확인2() throws Exception {
        //given
        지하철_노선_생성("테스트노선", "테스트색", "상행종점역", "하행종점역", 1000);
        StationResponse stationResponse = 역_생성("추가될역");
        Map<String, String> params = 구간_역_생성_및_노선등록_파라미터(2L, stationResponse.getId(), 500);
        ExtractableResponse<Response> extractableResponse = 구간_역_생성_및_노선등록_요청(params);
        구간_역_생성_및_노선등록됨(extractableResponse);

        //when
        ExtractableResponse<Response> response = 구간삭제_요청(stationResponse.getId());

        //then
        구간삭제됨(response);
    }

    @DisplayName("구간삭제 - 중간역을 삭제하는 경우")
    @Test
    public void 구간삭제_구간확인3() throws Exception {
        //given
        지하철_노선_생성("테스트노선", "테스트색", "상행종점역", "하행종점역", 1000);
        StationResponse stationResponse = 역_생성("추가될역");
        Map<String, String> params = 구간_역_생성_및_노선등록_파라미터(1L, stationResponse.getId(), 500);
        ExtractableResponse<Response> extractableResponse = 구간_역_생성_및_노선등록_요청(params);
        구간_역_생성_및_노선등록됨(extractableResponse);

        //when
        ExtractableResponse<Response> response = 구간삭제_요청(stationResponse.getId());

        //then
        구간삭제됨(response);
    }

    private void 구간_역_생성_및_노선등록(Long upStationId, Long downStationId, int distance) {
        // when
        Map<String, String> params = 구간_역_생성_및_노선등록_파라미터(upStationId, downStationId, distance);

        ExtractableResponse<Response> response = 구간_역_생성_및_노선등록_요청(params);

        // then
        구간_역_생성_및_노선등록됨(response);
    }

    private ExtractableResponse<Response> 구간_역_생성_및_노선등록_요청(Map<String, String> params) {
        return given()
                .log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
                .post(location + SectionAcceptanceTest.BASE_PATH)
        .then()
                .log().all()
                .extract();
    }

    private void 구간_역_생성_및_노선등록안됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    private void 구간_역_생성_및_노선등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(CREATED.value());
        LinesSubResponse linesSubResponse = 지하철_노선_조회();
        assertThat(linesSubResponse.getStations().size()).isEqualTo(3);
    }

    private Map<String, String> 구간_역_생성_및_노선등록_파라미터(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        params.put("distance", String.valueOf(distance));
        return params;
    }

    public static LineResponse 지하철_노선_생성(String name, String color, String upStationName, String downStationName, int distance) {
        StationResponse upStation = 역_생성(upStationName);
        StationResponse downStation = 역_생성(downStationName);

        Map<String, String> params = 지하철_노선_파라미터(name, color, String.valueOf(upStation.getId()),
                String.valueOf(downStation.getId()), String.valueOf(distance));
        ExtractableResponse<Response> extractableResponse = 지하철_노선_생성_요청(params);

        setLocation(extractableResponse.header("Location"));
        지하철_노선_생성됨(extractableResponse);
        return extractableResponse.jsonPath().getObject(".", LineResponse.class);
    }

    private static Map<String, String> 지하철_노선_파라미터(String name, String color, String upStationId,
                                                   String downStationId, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return params;
    }

    private Map<String, String> 지하철_노선_수정_파라미터(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return params;
    }

    private static void 지하철_노선_생성됨(ExtractableResponse<Response> extractableResponse) {
        assertThat(extractableResponse.statusCode()).isEqualTo(CREATED.value());
    }

    private void 지하철_노선_이름충돌로_생성되지_않음(ExtractableResponse<Response> extractableResponse) {
        assertThat(extractableResponse.statusCode()).isEqualTo(CONFLICT.value());
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> extractableResponse, Map<String, String> params) {
        assertThat(extractableResponse.statusCode()).isEqualTo(OK.value());
        ExtractableResponse<Response> response = 지하철_노선_조회_요청();
        LinesSubResponse linesSubResponse = response.jsonPath().getObject(".", LinesSubResponse.class);
        assertThat(linesSubResponse.getName()).isEqualTo(params.get("name"));
        assertThat(linesSubResponse.getColor()).isEqualTo(params.get("color"));
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> extractableResponse) {
        assertThat(extractableResponse.statusCode()).isEqualTo(NO_CONTENT.value());
        ExtractableResponse<Response> response = 지하철_노선_조회_요청();
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    private void 지하철_노선_목록_조회됨(ExtractableResponse<Response> extractableResponse, LineResponse... lineResponses) {
        assertThat(extractableResponse.statusCode()).isEqualTo(OK.value());
        List<LinesSubResponse> list = extractableResponse.jsonPath().getList(".");
        assertThat(list.size()).isEqualTo(lineResponses.length);
    }

    private void 지하철_노선_조회됨(ExtractableResponse<Response> extractableResponse, LineResponse lineResponse) {
        LinesSubResponse linesSubResponse = extractableResponse.jsonPath().getObject(".", LinesSubResponse.class);
        assertThat(extractableResponse.statusCode()).isEqualTo(OK.value());
        assertThat(linesSubResponse.getName()).isEqualTo(lineResponse.getName());
        assertThat(linesSubResponse.getColor()).isEqualTo(lineResponse.getColor());
        assertThat(linesSubResponse.getStations().size()).isEqualTo(2);
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        ExtractableResponse<Response> response =
                given()
                        .log().all()
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                        .get(BASE_PATH)
                .then()
                        .log().all()
                        .extract();
        return response;
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청() {
        ExtractableResponse<Response> response =
                given()
                        .log().all()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                        .get(getLocation())
                .then()
                        .log().all()
                        .extract();
        return response;
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(Map<String, String> params) {
        ExtractableResponse<Response> response =
                given()
                        .log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                        .put(getLocation())
                .then()
                        .log().all()
                        .extract();
        return response;
    }

    private ExtractableResponse<Response> 지하철_노선_삭제_요청() {
        ExtractableResponse<Response> response =
                given()
                        .log().all()
                .when()
                        .delete(getLocation())
                .then()
                        .log().all()
                        .extract();
        return response;
    }

    private static ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, String> params) {
        ExtractableResponse<Response> response =
                given()
                        .log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                        .post(BASE_PATH)
                .then()
                        .log().all()
                        .extract();
        return response;
    }

    public static String getLocation() {
        return location;
    }

    public static void setLocation(String location) {
        LineAcceptanceTest.location = location;
    }

    private static StationResponse 역_생성(String name) {
        Map<String, String> stationParams = StationAcceptanceTest.역_파라미터(name);
        ExtractableResponse<Response> stationExtractableResponse = StationAcceptanceTest.역_생성_요청(stationParams);
        StationAcceptanceTest.역_생성됨(stationExtractableResponse);
        return stationExtractableResponse.jsonPath().getObject(".", StationResponse.class);
    }

    public static LinesSubResponse 지하철_노선_조회() {
        ExtractableResponse<Response> extractableResponse = 지하철_노선_조회_요청();
        return extractableResponse.jsonPath().getObject(".", LinesSubResponse.class);
    }

    private void 구간삭제안됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 구간삭제_요청(Long stationId) {
        ExtractableResponse<Response> response =
                given()
                        .log().all()
                        .queryParam("stationId", stationId)
                        .when()
                        .delete(location + SectionAcceptanceTest.BASE_PATH)
                        .then()
                        .log().all()
                        .extract();
        return response;
    }

    private void 구간삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(OK.value());
        LinesSubResponse linesSubResponse = 지하철_노선_조회();
        assertThat(linesSubResponse.getStations().size()).isEqualTo(2);
    }
}
