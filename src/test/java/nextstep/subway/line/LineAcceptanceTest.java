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
        지하철_생성됨(response);
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

    private void 지하철_생성됨(final ExtractableResponse<Response> response) {
        StatusCodeCheckUtil.created(response);
        final Long id = response.jsonPath().getObject(".", LineResponse.class).getId();
        assertThat(response.header("Location")).isEqualTo("/lines/" + id);
    }

    private void 저장한_지하철_노선과_반환된_값들이_같은지_검증(
        final ExtractableResponse<Response> createResponse,
        final ExtractableResponse<Response> response
    ) {
        final LineResponse expectedLine = createResponse.jsonPath().getObject(".", LineResponse.class);
        final LineResponse actualLine = response.jsonPath().getObject(".", LineResponse.class);
        assertThat(actualLine).isEqualTo(expectedLine);
    }

    private void 저장한_지하철_노선_리스트와_반환된_값들이_같은지_검증(
        final ExtractableResponse<Response> createResponse1,
        final ExtractableResponse<Response> createResponse2,
        final ExtractableResponse<Response> response
    ) {
        final List<LineResponse> expectedLines = Stream.of(createResponse1, createResponse2)
            .map(it -> it.jsonPath().getObject(".", LineResponse.class))
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

    private Long 지하철역_생성_요청_후_아이디_반환(final String name) {
        final ExtractableResponse<Response> response = 지하철역_생성_요청(name);
        return response.jsonPath().getObject(".", StationResponse.class).getId();
    }
}
