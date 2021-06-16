package nextstep.subway.line;

import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    static final String LINE_API_ROOT = "/lines";

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선과_노선의_종점역_생성_요청("신분당선", "bg-red-600", "강남역", "광교역", 10);

        // then
        지하철_노선_생성됨(response, "신분당선", "bg-red-600");
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithAlreadyExistsName() {
        // given
        지하철_노선과_노선의_종점역_생성_요청("신분당선", "bg-red-600", "강남역", "광교역", 10);

        // when
        ExtractableResponse<Response> response = 지하철_노선과_노선의_종점역_생성_요청("신분당선", "bg-red-600", "방화역", "마천역", 10);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        LineResponse 신분당선 = 지하철_노선과_노선의_종점역_생성_요청("신분당선", "bg-red-600", "강남역", "광교역", 10)
            .as(LineResponse.class);
        LineResponse 오호선 = 지하철_노선과_노선의_종점역_생성_요청("5호선", "bg-purple-600", "방화역", "마천역", 10)
            .as(LineResponse.class);

        // when
        ExtractableResponse<Response> lineListResponse = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_응답됨(lineListResponse);
        지하철_노선_목록_포함됨(lineListResponse, 신분당선, 오호선);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        StationResponse 강남역 = 지하철역_생성_요청("강남역").as(StationResponse.class);
        StationResponse 광교역 = 지하철역_생성_요청("광교역").as(StationResponse.class);
        LineResponse 신분당선 = 지하철_노선_생성_요청("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 10)
            .as(LineResponse.class);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선.getId());

        // then
        지하철_노선_응답됨(response, 강남역.getId(), 광교역.getId());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        LineResponse 신분당선 = 지하철_노선과_노선의_종점역_생성_요청("신분당선", "bg-red-600", "강남역", "광교역", 10)
            .as(LineResponse.class);

        // when
        ExtractableResponse<Response> updatedResponse = 지하철_노선_수정_요청(신분당선.getId(), "신분당선", "bg-blue-600");

        // then
        지하철_노선_수정됨(updatedResponse);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        LineResponse 신분당선 = 지하철_노선과_노선의_종점역_생성_요청("신분당선", "bg-red-600", "강남역", "광교역", 10)
            .as(LineResponse.class);

        // when
        ExtractableResponse<Response> deletedResponse = 지하철_노선_제거_요청(신분당선.getId());

        // then
        지하철_노선_삭제됨(deletedResponse);
    }

    private ExtractableResponse<Response> 지하철_노선과_노선의_종점역_생성_요청(String name, String color, String upStationName, String downStationName, int distance) {
        StationResponse createdUpStation = 지하철역_생성_요청(upStationName).as(StationResponse.class);
        StationResponse createdDownStation = 지하철역_생성_요청(downStationName).as(StationResponse.class);
        return 지하철_노선_생성_요청(name, color, createdUpStation.getId(), createdDownStation.getId(), distance);
    }

    static ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color, long upStationId, long downStationId, int distance) {
        return RestAssured
            .given().log().all()
            .body(createBody(name, color, upStationId, downStationId, distance))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post(LINE_API_ROOT)
            .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
            .given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().get(LINE_API_ROOT)
            .then().log().all().extract();
    }

    static ExtractableResponse<Response> 지하철_노선_조회_요청(Long lineId) {
        return RestAssured
            .given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().get(LINE_API_ROOT + "/" + lineId)
            .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(long lineId, String lineName, String lineColor) {
        return RestAssured
            .given().log().all()
            .body(createBody(lineName, lineColor))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put(LINE_API_ROOT + "/" + lineId)
            .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(long lineId) {
        return RestAssured
            .given().log().all()
            .when().delete(LINE_API_ROOT + "/" + lineId)
            .then().log().all().extract();
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> deletedResponse) {
        assertThat(deletedResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response, String lineName, String lineColor) {
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        Assertions.assertThat(response.header("Location")).isNotBlank();

        LineResponse lineResponse = response.as(LineResponse.class);
        Assertions.assertThat(lineResponse.getId()).isNotNull();
        Assertions.assertThat(lineResponse.getName()).isEqualTo(lineName);
        Assertions.assertThat(lineResponse.getColor()).isEqualTo(lineColor);
        Assertions.assertThat(lineResponse.getCreatedDate()).isNotNull();
        Assertions.assertThat(lineResponse.getModifiedDate()).isNotNull();
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> lineListResponse) {
        assertThat(lineListResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> listResponse, LineResponse... lines) {
        List<Long> expectedLineIds = Stream.of(lines)
            .map(LineResponse::getId)
            .collect(Collectors.toList());

        List<Long> resultLineIds = listResponse.jsonPath()
            .getList(".", LineResponse.class)
            .stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> updatedResponse) {
        Assertions.assertThat(updatedResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    static void 지하철_노선_응답됨(ExtractableResponse<Response> findResponse, Long... stationIds) {
        assertThat(findResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findResponse.as(LineResponse.class)).isNotNull();
        assertThat(findResponse.jsonPath().getList("stations.id", Long.class)).containsExactly(stationIds);
    }

    private static Map<String, Object> createBody(String name, String color) {
        Map<String, Object> params = new HashMap<>();
        params.put("color", color);
        params.put("name", name);
        return params;
    }

    private static Map<String, Object> createBody(String name, String color, long upStationId,
        long downStationId, int distance) {
        Map<String, Object> params = createBody(name, color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return params;
    }
}
