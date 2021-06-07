package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesSubResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private static final String BASE_PATH = "/lines";

    private String location;

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
        지하철_노선_생성("5호선", "purple", "방화역", "하남검단산역");

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
        LineResponse line1 = 지하철_노선_생성("5호선", "purple", "방화역", "하남검단산역");
        LineResponse line2 = 지하철_노선_생성("4호선", "sky", "당고개역", "오이도역");

        // when
        ExtractableResponse<Response> extractableResponse = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_조회됨(extractableResponse, line1, line2);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        //given
        LineResponse lineResponse = 지하철_노선_생성("5호선", "purple", "방화역", "하남검단산역");

        // when
        ExtractableResponse<Response> extractableResponse = 지하철_노선_조회_요청();

        // then
        지하철_노선_조회됨(extractableResponse, lineResponse);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        지하철_노선_생성("5호선", "purple", "방화역", "하남검단산역");

        // when
        Map<String, String> params = 지하철_노선_수정_파라미터("4호선", "sky");
        ExtractableResponse<Response> extractableResponse = 지하철_노선_수정_요청(params);

        // then
        지하철_노선_수정됨(extractableResponse);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        지하철_노선_생성("5호선", "purple", "방화역", "하남검단산역");

        // when
        ExtractableResponse<Response> extractableResponse = 지하철_노선_삭제_요청();

        // then
        지하철_노선_삭제됨(extractableResponse);
    }

    private LineResponse 지하철_노선_생성(String name, String color, String upStationName, String downStationName) {
        StationResponse upStation = 역_생성(upStationName);
        StationResponse downStation = 역_생성(downStationName);

        Map<String, String> params = 지하철_노선_파라미터(name, color, String.valueOf(upStation.getId()),
                String.valueOf(downStation.getId()), "1000");
        ExtractableResponse<Response> extractableResponse = 지하철_노선_생성_요청(params);

        setLocation(extractableResponse.header("Location"));
        지하철_노선_생성됨(extractableResponse);
        return extractableResponse.jsonPath().getObject(".", LineResponse.class);
    }

    private Map<String, String> 지하철_노선_파라미터(String name, String color, String upStationId,
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

    private void 지하철_노선_생성됨(ExtractableResponse<Response> extractableResponse) {
        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_노선_이름충돌로_생성되지_않음(ExtractableResponse<Response> extractableResponse) {
        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> extractableResponse) {
        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> extractableResponse) {
        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void 지하철_노선_목록_조회됨(ExtractableResponse<Response> extractableResponse, LineResponse... lineResponses) {
        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<LinesSubResponse> list = extractableResponse.jsonPath().getList(".");
        assertThat(list.size()).isEqualTo(lineResponses.length);
    }

    private void 지하철_노선_조회됨(ExtractableResponse<Response> extractableResponse, LineResponse lineResponse) {
        LinesSubResponse linesSubResponse = extractableResponse.jsonPath().getObject(".", LinesSubResponse.class);
        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(linesSubResponse.getName()).isEqualTo(lineResponse.getName());
        assertThat(linesSubResponse.getColor()).isEqualTo(lineResponse.getColor());
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

    private ExtractableResponse<Response> 지하철_노선_조회_요청() {
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

    private ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, String> params) {
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private StationResponse 역_생성(String name) {
        Map<String, String> stationParams = StationAcceptanceTest.역_파라미터(name);
        ExtractableResponse<Response> stationExtractableResponse = StationAcceptanceTest.역_생성_요청(stationParams);
        StationAcceptanceTest.역_생성됨(stationExtractableResponse);
        return stationExtractableResponse.jsonPath().getObject(".", StationResponse.class);
    }
}
