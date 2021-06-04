package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

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
        String 강남역 = 지하철_역_생성("강남역");
        String 잠실역 = 지하철_역_생성("잠실역");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("bg-red-600", "신분당선", 강남역, 잠실역, "10");

        // then
        // 지하철_노선_생성됨
        지하철_노선_생성됨(response);

    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_역_등록
        String 강남역 = 지하철_역_생성("강남역");
        String 잠실역 = 지하철_역_생성("잠실역");
        // 지하철_노선_등록되어_있음
        지하철_노선_생성_요청("bg-red-600", "신분당선", 강남역, 잠실역, "10");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("bg-red-600", "신분당선", 강남역, 잠실역, "10");

        // then
        // 지하철_노선_생성_실패됨
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_역_등록
        String 강남역 = 지하철_역_생성("강남역");
        String 잠실역 = 지하철_역_생성("잠실역");
        String 광교역 = 지하철_역_생성("광교역");

        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse1 = 지하철_노선_생성_요청("bg-red-600", "신분당선", 강남역, 광교역, "10");

        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse2 = 지하철_노선_생성_요청("bg-green-600", "2호선", 강남역, 잠실역, "10");

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        // 지하철_노선_목록_응답됨
        지하철_노선_목록_성공_응답됨(response);
        // 지하철_노선_목록_포함됨
        지하철_노선_목록_포함됨(createResponse1, createResponse2, response);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_역_등록
        String 강남역 = 지하철_역_생성("강남역");
        String 잠실역 = 지하철_역_생성("잠실역");
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청("bg-green-600", "2호선", 강남역, 잠실역, "10");

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createResponse);

        // then
        // 지하철_노선_응답됨
        지하철_노선_목록_성공_응답됨(response);
        지하철_노선_목록_갯수(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_역_등록
        String 강남역 = 지하철_역_생성("강남역");
        String 잠실역 = 지하철_역_생성("잠실역");
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청("bg-red-600", "신분당선", 강남역, 잠실역, "10");

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(createResponse, "bg-red-600", "신분당선");

        // then
        // 지하철_노선_수정됨
        지하철_노선_목록_성공_응답됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_역_등록
        String 강남역 = 지하철_역_생성("강남역");
        String 잠실역 = 지하철_역_생성("잠실역");
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청("bg-red-600", "신분당선", 강남역, 잠실역, "10");

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = 자하철_노선_제거_요청(createResponse);

        // then
        // 지하철_노선_삭제됨
        지하철_노선_삭제됨(response);
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(String lineColor, String lineName, String upStationId, String downStationId, String distance) {
        Map<String, String> params = createLineParam(lineColor, lineName, upStationId, downStationId, distance);
        return post("/lines", params);
    }

    private Map<String, String> createLineParam(String lineColor, String lineName, String upStationId, String downStationId, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("color", lineColor);
        params.put("name", lineName);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);


        return params;
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return get("/lines");
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(ExtractableResponse<Response> createResponse) {
        String uri = createResponse.header("Location");
        return get(uri);
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(ExtractableResponse<Response> createResponse, String lineColor, String lineName) {
        String uri = createResponse.header("Location");

        Map<String, String> params = createLineParam(lineColor, lineName, null, null, null);

        return put(uri, params);
    }

    private ExtractableResponse<Response> 자하철_노선_제거_요청(ExtractableResponse<Response> createResponse) {
        String uri = createResponse.header("Location");
        return delete(uri);
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_노선_목록_성공_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> createResponse1, ExtractableResponse<Response> createResponse2, ExtractableResponse<Response> response) {
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }


    private String 지하철_역_생성(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);
        ExtractableResponse<Response> response = post("/stations", params);
        return response.header("Location").split("/")[2];
    }


    private void 지하철_노선_목록_갯수(ExtractableResponse<Response> response) {
        long count = response.jsonPath().getObject("$.", LineResponse.class)
                .getStations().size();
        assertThat(count).isEqualTo(2);
    }
}
