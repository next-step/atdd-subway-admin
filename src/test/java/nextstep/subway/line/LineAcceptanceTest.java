package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.utils.LocationUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> extract = 지하철_노선_생성_요청("신분당선", "bg-red-600");

        // then
        // 지하철_노선_생성됨
        assertThat(extract.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(extract.header("Location")).isNotBlank();
    }

    @DisplayName("지하철 노선을 생성 시 종점역(상행,하행), 거리를 함께 추가한다")
    @Test
    void createLineIncludeUpAndDownStation() {

        // given
        // 지하철_역_생성_요청
        Long 강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역");
        Long 양재역 = StationAcceptanceTest.지하철역_등록되어_있음("양재역");

        // 지하철_노선_생성_요청
        ExtractableResponse<Response> extract = 지하철_노선_생성_요청("신분당선", "bg-red-600", 강남역, 양재역);
        Long extractId = getLineResponse(extract).getId();

        // then
        // 지하철_노선_생성됨
        assertThat(extract.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        ExtractableResponse<Response> thenResponse = 지하철_노선_조회_요청(extractId);

        LineResponse thenLineResponse = getLineResponse(thenResponse);
        assertThat(thenLineResponse.getStations()).hasSize(2);
    }

    @DisplayName("지하철 노선을 생성 시 종점역(상행,하행)가 같으면 에러를 반환한다")
    @Test
    void createLineInputSameSectionException() {
        // given
        // 지하철_역_생성_요청
        Long 강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> extract = 지하철_노선_생성_요청("신분당선", "bg-red-600", 강남역, 강남역);

        // then
        // 지하철_노선_생성 실패됨
        assertThat(extract.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineExistLineName() {
        // given
        // 지하철_노선_등록되어_있음
        String lineName = "신분당선";
        지하철_노선_등록되어_있음(lineName, "bg-red-600");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineName, "bg-red-600");

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private static ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color, Long upStationId, Long downStationId) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId.toString());
        params.put("downStationId", downStationId.toString());
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        List<Long> existResponseIds = new ArrayList<>();
        // given
        // 지하철_노선_등록되어_있음
        existResponseIds.add(지하철_노선_등록되어_있음("신분당선", "bg-red-600"));
        // 지하철_노선_등록되어_있음
        existResponseIds.add(지하철_노선_등록되어_있음("2호선", "bg-orange-600"));

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        List<Long> responseIds = 지하철_노선_목록_포함됨(response);
        assertThat(existResponseIds).containsAll(responseIds);
    }

    private static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given().log().all().
                when().
                get("/lines").
                then().
                log().all().
                extract();
    }

    private static List<Long> 지하철_노선_목록_포함됨(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        long lineId = 지하철_노선_등록되어_있음("신분당선", "bg-red-600");

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);
        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선 이름을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        long lineId = 지하철_노선_등록되어_있음("신분당선", "bg-red-600");

        // when
        // 지하철_노선_수정_요청
        String editName = "3호선";
        String editColor = "bg-orange";
        지하철_노선_수정_요청(lineId, editName, editColor);

        // then
        // 지하철_노선_수정됨
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);

        LineResponse lineResponse = getLineResponse(response);
        assertThat(lineResponse.getName()).isEqualTo(editName);
        assertThat(lineResponse.getColor()).isEqualTo(editColor);
    }

    @DisplayName("지하철 노선을 구간 정보를 수정한다.")
    @Test
    void updateLineAndSection() {

        Long 강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역");
        Long 양재역 = StationAcceptanceTest.지하철역_등록되어_있음("양재역");
        Long 판교역 = StationAcceptanceTest.지하철역_등록되어_있음("판교역");
        // given
        // 지하철_노선_등록되어_있음
        long lineId = 지하철_노선_등록되어_있음("신분당선", "bg-red-600", 강남역, 양재역);

        // when
        // 지하철_노선_수정_요청
        String editName = "3호선";
        String editColor = "bg-orange";
        지하철_노선_수정_요청(lineId, editName, editColor, 강남역, 판교역);

        // then
        // 지하철_노선_수정됨
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);

        LineResponse lineResponse = getLineResponse(response);
        assertThat(lineResponse.getName()).isEqualTo(editName);
        assertThat(lineResponse.getColor()).isEqualTo(editColor);
        assertThat(lineResponse.isContainsStationIds(Arrays.asList(강남역, 판교역))).isTrue();

    }

    private static ExtractableResponse<Response> 지하철_노선_수정_요청(long lineId, String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return RestAssured.given().log().all().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                put("/lines/{id}", lineId).
                then().
                log().all().
                extract();
    }

    private static ExtractableResponse<Response> 지하철_노선_수정_요청(long lineId, String name, String color, Long upStationId, Long downStationId) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId.toString());
        params.put("downStationId", downStationId.toString());
        return RestAssured.given().log().all().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                put("/lines/{id}", lineId).
                then().
                log().all().
                extract();
    }

    private LineResponse getLineResponse(ExtractableResponse<Response> response) {
        return response.jsonPath()
                .getObject(".", LineResponse.class);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        long lineId = 지하철_노선_등록되어_있음("신분당선", "bg-red-600");

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(lineId);

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private static ExtractableResponse<Response> 지하철_노선_제거_요청(long lineId) {
        return RestAssured.given().log().all().
                when().
                delete("/lines/{id}", lineId).
                then().
                log().all().
                extract();
    }

    private static Long 지하철_노선_등록되어_있음(String lineName, String color) {
        return LocationUtil.getLocation(지하철_노선_생성_요청(lineName, color));
    }

    private static Long 지하철_노선_등록되어_있음(String lineName, String color, Long upStationId, Long downStationId) {
        return LocationUtil.getLocation(지하철_노선_생성_요청(lineName, color, upStationId, downStationId));
    }

    private static ExtractableResponse<Response> 지하철_노선_조회_요청(long lineId) {
        return RestAssured.given().log().all().
                when().
                get("/lines/{id}", lineId).
                then().
                log().all().
                extract();
    }

}
