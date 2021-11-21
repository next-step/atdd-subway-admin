package nextstep.subway.line;

import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.RequestUtil;
import nextstep.subway.utils.StatusCodeCheckUtil;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    public static final List<String> EXPECTED_FIELDS =
        Arrays.asList("id", "name", "color", "createdDate", "modifiedDate", "stations");

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        final ExtractableResponse<Response> response =
            지하철_노선_생성_요청("신분당선", "red", "강남역", "역삼역", 10);

        // then
        지하철_노선_생성됨(response);
        기대되는_모든_필드가_있는지_검증(response, ".");
        지하철역_정렬_검증(response, "stations", "강남역", "역삼역");
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_생성_요청("신분당선", "red", "강남역", "역삼역", 10);

        // when
        final ExtractableResponse<Response> response =
            지하철_노선_생성_요청("신분당선", "red", "신도림역", "송내역", 15);

        // then
        StatusCodeCheckUtil.badRequest(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        final ExtractableResponse<Response> createResponse1 =
            지하철_노선_생성_요청("신분당선", "red", "강남역", "역삼역", 10);
        final ExtractableResponse<Response> createResponse2 =
            지하철_노선_생성_요청("1호선", "indigo", "신도림역", "송내역", 15);

        // when
        final ExtractableResponse<Response> response = 지하철_노선_목록_조회();

        // then
        StatusCodeCheckUtil.ok(response);
        저장한_지하철_노선_리스트와_반환된_값들이_같은지_검증(createResponse1, createResponse2, response);
        기대되는_모든_필드가_있는지_검증(response, "[0]");
        지하철역_정렬_검증(response, "stations[0]", "강남역", "역삼역");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        final ExtractableResponse<Response> createResponse =
            지하철_노선_생성_요청("신분당선", "red", "강남역", "역삼역", 10);

        // when
        final ExtractableResponse<Response> response = 지하철_노선_조회(createResponse);

        // then
        StatusCodeCheckUtil.ok(response);
        저장한_지하철_노선과_반환된_값들이_같은지_검증(createResponse, response);
        기대되는_모든_필드가_있는지_검증(response, ".");
        지하철역_정렬_검증(response, "stations", "강남역", "역삼역");
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        final ExtractableResponse<Response> createResponse =
            지하철_노선_생성_요청("신분당선", "red", "강남역", "역삼역", 10);

        // when
        final ExtractableResponse<Response> response = 지하철_노선_수정_요청(createResponse, "구분당선", "blue");

        // then
        StatusCodeCheckUtil.ok(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        final ExtractableResponse<Response> createResponse =
            지하철_노선_생성_요청("신분당선", "red", "강남역", "역삼역", 10);

        // when
        final ExtractableResponse<Response> response = 지하철_노선_삭제_요청(createResponse);

        // then
        StatusCodeCheckUtil.noContent(response);
    }

    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void addSection() {
        // given
        final ExtractableResponse<Response> createResponse =
            지하철_노선_생성_요청("신분당선", "red", "강남역", "역삼역", 10);
        final LineResponse line = 지하철_노선_객체_추출(createResponse);
        final Long lineId = line.getId();
        final Long upStationId = 상행_종점역_아이디_추출(line);
        final Long downStationId = 지하철역_생성_요청_후_아이디_반환("판교역");

        // when
        final ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_등록_요청(lineId, upStationId, downStationId, 5);

        // then
        지하철_구간_추가됨(response, lineId, "강남역", "판교역", "역삼역");
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 예외 발생")
    @Test
    void addSectionWithLongDistance() {
        // given
        final ExtractableResponse<Response> createResponse =
            지하철_노선_생성_요청("신분당선", "red", "강남역", "역삼역", 10);
        final LineResponse line = 지하철_노선_객체_추출(createResponse);
        final Long lineId = line.getId();
        final Long upStationId = 상행_종점역_아이디_추출(line);
        final Long downStationId = 지하철역_생성_요청_후_아이디_반환("판교역");

        // when
        final ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_등록_요청(lineId, upStationId, downStationId, 10);

        // then
        StatusCodeCheckUtil.badRequest(response);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 예외 발생")
    @Test
    void addSectionThatAlreadyExists() {
        // given
        final ExtractableResponse<Response> createResponse =
            지하철_노선_생성_요청("신분당선", "red", "강남역", "역삼역", 10);
        final LineResponse line = 지하철_노선_객체_추출(createResponse);
        final Long lineId = line.getId();
        final Long upStationId = 상행_종점역_아이디_추출(line);
        final Long downStationId = 하행_종점역_아이디_추출(line);

        // when
        final ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_등록_요청(lineId, upStationId, downStationId, 5);

        // then
        StatusCodeCheckUtil.badRequest(response);
    }

    @DisplayName("추가하려고 하는 구간이 기존의 구간과 연결이 불가능하면 예외 발생")
    @Test
    void addSectionNotConnected() {
        // given
        final ExtractableResponse<Response> createResponse =
            지하철_노선_생성_요청("신분당선", "red", "강남역", "역삼역", 10);
        final LineResponse line = 지하철_노선_객체_추출(createResponse);
        final Long lineId = line.getId();
        final Long upStationId = 지하철역_생성_요청_후_아이디_반환("판교역");
        final Long downStationId = 지하철역_생성_요청_후_아이디_반환("광교역");

        // when
        final ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_등록_요청(lineId, upStationId, downStationId, 5);

        // then
        StatusCodeCheckUtil.badRequest(response);
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(final String name, final String color,
        final String upStationName, final String downStationName, final int distance) {

        final Long upStationId = 지하철역_생성_요청_후_아이디_반환(upStationName);
        final Long downStationId = 지하철역_생성_요청_후_아이디_반환(downStationName);

        final Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RequestUtil.post("/lines", params);
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회() {
        return RequestUtil.get("/lines");
    }

    private ExtractableResponse<Response> 지하철_노선_조회(final ExtractableResponse<Response> createResponse) {
        final String uri = createResponse.header("Location");
        return RequestUtil.get(uri);
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(
        final ExtractableResponse<Response> createResponse, final String name, final String color
    ) {
        final String uri = createResponse.header("Location");
        final Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RequestUtil.put(uri, params);
    }

    private ExtractableResponse<Response> 지하철_노선_삭제_요청(final ExtractableResponse<Response> createResponse) {
        final String uri = createResponse.header("Location");
        return RequestUtil.delete(uri);
    }

    private void 지하철_노선_생성됨(final ExtractableResponse<Response> response) {
        StatusCodeCheckUtil.created(response);
        final Long id = 지하철_노선_객체_추출(response).getId();
        assertThat(response.header("Location")).isEqualTo("/lines/" + id);
    }

    private void 저장한_지하철_노선과_반환된_값들이_같은지_검증(
        final ExtractableResponse<Response> createResponse,
        final ExtractableResponse<Response> response
    ) {
        final LineResponse expectedLine = 지하철_노선_객체_추출(createResponse);
        final LineResponse actualLine = 지하철_노선_객체_추출(response);
        assertThat(actualLine).isEqualTo(expectedLine);
    }

    private void 저장한_지하철_노선_리스트와_반환된_값들이_같은지_검증(
        final ExtractableResponse<Response> createResponse1,
        final ExtractableResponse<Response> createResponse2,
        final ExtractableResponse<Response> response
    ) {
        final List<LineResponse> expectedLines = Stream.of(createResponse1, createResponse2)
            .map(this::지하철_노선_객체_추출)
            .collect(Collectors.toList());
        final List<LineResponse> actualLines = response.jsonPath().getList(".", LineResponse.class);
        assertThat(actualLines).containsAll(expectedLines);
    }

    private void 기대되는_모든_필드가_있는지_검증(final ExtractableResponse<Response> response, final String targetPath) {
        final Set<String> actualFields = response.jsonPath().getMap(targetPath, String.class, String.class).keySet();
        assertThat(actualFields).containsAll(EXPECTED_FIELDS);
    }

    private void 지하철역_정렬_검증(
        final ExtractableResponse<Response> response,
        final String stationPath,
        final String... expectedStationNames
    ) {
        final List<StationResponse> stations = response.jsonPath().getList(stationPath, StationResponse.class);
        final List<String> stationNames = stations.stream()
            .map(StationResponse::getName)
            .collect(Collectors.toList());

        assertThat(stationNames).containsExactly(expectedStationNames);
    }

    private LineResponse 지하철_노선_객체_추출(final ExtractableResponse<Response> response) {
        return response.jsonPath().getObject(".", LineResponse.class);
    }

    private Long 지하철역_생성_요청_후_아이디_반환(final String name) {
        final ExtractableResponse<Response> response = 지하철역_생성_요청(name);
        return response.jsonPath().getObject(".", StationResponse.class).getId();
    }

    private ExtractableResponse<Response> 지하철_노선에_지하철_구간_등록_요청(
        final Long lineId, final Long upStationId, final Long downStationId, final int distance
    ) {
        final Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RequestUtil.post("/lines/" + lineId + "/sections", params);
    }

    private Long 상행_종점역_아이디_추출(final LineResponse line) {
        return line.getStations().get(0).getId();
    }

    private Long 하행_종점역_아이디_추출(final LineResponse line) {
        final List<StationResponse> stations = line.getStations();
        return stations.get(stations.size() - 1).getId();
    }

    private void 지하철_구간_추가됨(
        final ExtractableResponse<Response> response, final Long lineId, final String... expectedStationNames
    ) {
        StatusCodeCheckUtil.created(response);
        final ExtractableResponse<Response> searchedLineResponse = RequestUtil.get("/lines/" + lineId);
        지하철역_정렬_검증(searchedLineResponse, "stations", expectedStationNames);
    }
}
