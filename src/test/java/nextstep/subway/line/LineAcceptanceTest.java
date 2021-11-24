package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineFindResponse;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static nextstep.subway.station.StationAcceptanceTest.지하철역_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        StationResponse 강남역 = 지하철역_등록되어_있음("강남역");
        StationResponse 역삼역 = 지하철역_등록되어_있음("역삼역");

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("bg-red-600", "신분당선", 강남역, 역삼역, "10");

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        StationResponse 강남역 = 지하철역_등록되어_있음("강남역");
        StationResponse 역삼역 = 지하철역_등록되어_있음("역삼역");
        지하철_노선_등록되어_있음("bg-red-600", "신분당선", 강남역, 역삼역, "10");

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("bg-red-600", "신분당선", 강남역, 역삼역, "10");

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        StationResponse 강남역 = 지하철역_등록되어_있음("강남역");
        StationResponse 역삼역 = 지하철역_등록되어_있음("역삼역");
        StationResponse 서초역 = 지하철역_등록되어_있음("서초역");
        StationResponse 교대역 = 지하철역_등록되어_있음("교대역");

        String name1 = "신분당선";
        String name2 = "2호선";
        지하철_노선_등록되어_있음("bg-red-600", name1, 강남역, 역삼역, "10");
        지하철_노선_등록되어_있음("bg-green-600", name2, 서초역, 교대역, "20");

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(response, name1, name2);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        String name1 = "강남역";
        String name2 = "역삼역";
        StationResponse 강남역 = 지하철역_등록되어_있음(name1);
        StationResponse 역삼역 = 지하철역_등록되어_있음(name2);
        지하철_노선_등록되어_있음("bg-red-600", "신분당선", 강남역, 역삼역, "10");

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(강남역.getId());

        // then
        지하철_노선_응답됨(response);
        지하철역_목록_포함됨(name1, name2, response);
    }

    @DisplayName("조회한 노선이 존재하지 않을 경우 실패한다.")
    @Test
    void getLine_존재하지_않는_노선() {
        // given
        // 지하철_노선_등록되어_있지 않음

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(1L);

        // then
        지하철_노선_조회_실패됨(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        StationResponse 강남역 = 지하철역_등록되어_있음("강남역");
        StationResponse 역삼역 = 지하철역_등록되어_있음("역삼역");
        LineResponse 신분당선 = 지하철_노선_등록되어_있음("bg-red-600", "신분당선", 강남역, 역삼역, "10");

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청("bg-blue-600", "구분당선", 신분당선.getId());

        // then
        지하철_노선_수정됨(response);
    }

    @DisplayName("수정할 노선이 존재하지 않는 경우 실패한다.")
    @Test
    void updateLine_존재하지_않는_노선() {
        // given
        // 지하철_노선_등록되어_있지_않음

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청("bg-blue-600", "구분당선", 1L);

        // then
        지하철_노선_수정_실패됨(response, HttpStatus.NOT_FOUND);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 수정하는 경우 실패한다.")
    @Test
    void updateLine_기존_존재하는_노선_이름() {
        // given
        StationResponse 강남역 = 지하철역_등록되어_있음("강남역");
        StationResponse 역삼역 = 지하철역_등록되어_있음("역삼역");
        StationResponse 서초역 = 지하철역_등록되어_있음("서초역");
        StationResponse 교대역 = 지하철역_등록되어_있음("교대역");
        LineResponse 신분당선 = 지하철_노선_등록되어_있음("bg-red-600", "신분당선", 강남역, 역삼역, "10");
        지하철_노선_등록되어_있음("bg-green-600", "2호선", 서초역, 교대역, "20");

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청("bg-blue-600", "2호선", 신분당선.getId());

        // then
        지하철_노선_수정_실패됨(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        StationResponse 강남역 = 지하철역_등록되어_있음("강남역");
        StationResponse 역삼역 = 지하철역_등록되어_있음("역삼역");
        LineResponse 신분당선 = 지하철_노선_등록되어_있음("bg-red-600", "신분당선", 강남역, 역삼역, "10");

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(신분당선.getId());

        // then
        지하철_노선_삭제됨(response);
    }

    @DisplayName("제거할 노선이 존재하지 않는 경우")
    @Test
    void deleteLine_존재하지_않는_노선() {
        // given
        // 지하철_노선_등록되어_있지_않음

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(1L);

        // then
        지하철_노선_삭제_실패됨(response);
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(String color, String name, StationResponse upStationResponse, StationResponse downStationResponse, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("color", color);
        params.put("name", name);
        params.put("upStationId", String.valueOf(upStationResponse.getId()));
        params.put("downStationId", String.valueOf(downStationResponse.getId()));
        params.put("distance", distance);

        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private LineResponse 지하철_노선_등록되어_있음(String color, String name, StationResponse upStationResponse, StationResponse downStationResponse, String distance) {
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(color, name, upStationResponse, downStationResponse, distance);
        지하철_노선_생성됨(response);
        return response.jsonPath().getObject(".", LineResponse.class);
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all().extract();
        return response;
    }

    private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response, String name1, String name2) {
        List<LineFindResponse> lineFindResponses = response.jsonPath()
                .getList(".", LineFindResponse.class);

        List<String> names = lineFindResponses.stream()
                .map(lineFindResponse -> lineFindResponse.getName())
                .collect(Collectors.toList());

        assertThat(names).containsExactly(name1, name2);
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/{id}", id)
                .then().log().all().extract();
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_조회_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(String color, String name, Long id) {
        Map<String, String> params = new HashMap<>();
        params.put("color", color);
        params.put("name", name);
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{id}", id)
                .then().log().all().extract();
        return response;
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_수정_실패됨(ExtractableResponse<Response> response, HttpStatus httpStatus) {
        assertThat(response.statusCode()).isEqualTo(httpStatus.value());
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(Long id) {
        return RestAssured
                .given().log().all()
                .when().delete("/lines/{id}", id)
                .then().log().all().extract();
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void 지하철_노선_삭제_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private void 지하철역_목록_포함됨(String name1, String name2, ExtractableResponse<Response> response) {
        List<StationResponse> stationResponses = response.jsonPath()
                .getList("stations", StationResponse.class);

        List<String> names = stationResponses.stream()
                .map(s -> s.getName()).collect(Collectors.toList());

        assertThat(names).containsExactly(name1, name2);
    }
}