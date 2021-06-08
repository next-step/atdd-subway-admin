package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static nextstep.subway.line.LineSteps.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private static final Map<String, String> 강남역 = new HashMap<>();
    private static final Map<String, String> 양재역 = new HashMap<>();
    private static final Map<String, String> 신림역 = new HashMap<>();
    private static final Map<String, String> 봉천역 = new HashMap<>();

    static {
        강남역.put("name", "강남역");
        양재역.put("name", "양재역");
        신림역.put("name", "신림역");
        봉천역.put("name", "봉천역");
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        LineRequest 신분당선 = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);

        // when
        ExtractableResponse<Response> 생성된_신분당선 = 지하철_노선_생성_요청(신분당선, 강남역, 양재역);

        // then
        지하철_노선_생성됨(생성된_신분당선);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        LineRequest 신분당선 = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        지하철_노선_생성_요청(신분당선, 강남역, 양재역);

        // when
        ExtractableResponse<Response> 생성된_신분당선 = 지하철_노선_생성_요청(신분당선);

        // then
        지하철_노선_생성_실패됨(생성된_신분당선);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        LineRequest 신분당선 = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        LineRequest 이호선 = new LineRequest("2호선", "bg-green-600", 3L, 4L, 10);
        ExtractableResponse<Response> 생성된_신분당선 = 지하철_노선_생성_요청(신분당선, 강남역, 양재역);
        ExtractableResponse<Response> 생성된_이호선 = 지하철_노선_생성_요청(이호선, 신림역, 봉천역);

        // when
        ExtractableResponse<Response> 조회된_노선_목록 = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_응답됨(조회된_노선_목록);
        지하철_노선_목록_포함됨(생성된_신분당선, 생성된_이호선, 조회된_노선_목록);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        LineRequest 신분당선 = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        ExtractableResponse<Response> 생성된_신분당선 = 지하철_노선_생성_요청(신분당선, 강남역, 양재역);
        int expectedStationCount = 2;

        // when
        ExtractableResponse<Response> 조회된_노선 = 지하철_노선_조회_요청(생성된_신분당선);

        // then
        지하철_노선_응답됨(조회된_노선, 신분당선, expectedStationCount);
        지하철_노선_역_응답됨(조회된_노선, 신분당선, 강남역, 양재역);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        LineRequest 신분당선 = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        ExtractableResponse<Response> 생성된_신분당선 = 지하철_노선_생성_요청(신분당선, 강남역, 양재역);
        Map<String, String> params = createParams("구분당선", "bg-blue-600");

        // when
        ExtractableResponse<Response> 수정된_노선 = 지하철_노선_수정_요청(생성된_신분당선, params);

        // then
        지하철_노선_수정됨(수정된_노선);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        LineRequest 신분당선 = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        ExtractableResponse<Response> 생성된_신분당선 = 지하철_노선_생성_요청(신분당선, 강남역, 양재역);

        // when
        ExtractableResponse<Response> 제거된_노선 = 지하철_노선_제거_요청(생성된_신분당선);

        // then
        지하철_노선_삭제됨(제거된_노선);
    }

    private Map<String, String> createParams(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return params;
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> createResponse1, ExtractableResponse<Response> createResponse2,
                               ExtractableResponse<Response> response) {

        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response, LineRequest lineRequest, int expectedStationCount) {
        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(lineResponse.getName()).isEqualTo(lineRequest.getName()),
                () -> assertThat(lineResponse.getColor()).isEqualTo(lineRequest.getColor()),
                () -> assertThat(lineResponse.getStations().size()).isEqualTo(expectedStationCount)
        );
    }

    private void 지하철_노선_역_응답됨(ExtractableResponse<Response> response, LineRequest lineRequest,
                              Map<String, String> upStationParam, Map<String, String> downStationParam) {

        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
        StationResponse upStation = lineResponse.getStations().get(0);
        assertAll(
                () -> assertThat(upStation.getId()).isEqualTo(lineRequest.getUpStationId()),
                () -> assertThat(upStation.getName()).isEqualTo(upStationParam.get("name"))
        );

        StationResponse downStation = lineResponse.getStations().get(1);
        assertAll(
                () -> assertThat(downStation.getId()).isEqualTo(lineRequest.getDownStationId()),
                () -> assertThat(downStation.getName()).isEqualTo(downStationParam.get("name"))
        );
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
