package nextstep.subway.line;

import nextstep.subway.station.domain.Station;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nextstep.subway.line.LineRestAssured.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        ExtractableResponse<Response> stationCreateResponse01 = 역_생성_요청_01();
        ExtractableResponse<Response> stationCreateResponse02 = 역_생성_요청_02();

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청_01(stationCreateResponse01, stationCreateResponse02, 10);

        // then
        지하철_노선_생성_성공(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        ExtractableResponse<Response> lineCreateResponse = 지하철_노선_생성_요청_01(역_생성_요청_01(), 역_생성_요청_02(), 10);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청_01(lineCreateResponse, 10);

        // then
        지하철_노선_생성_실패(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> lineCreateResponse01 = 지하철_노선_생성_요청_01(역_생성_요청_01(), 역_생성_요청_02(), 10);
        ExtractableResponse<Response> lineCreateResponse02 = 지하철_노선_생성_요청_02(역_생성_요청_03(), 역_생성_요청_04(), 20);

        // when
        ExtractableResponse<Response> lineGetResponse = 지하철_노선_목록_조회_요청("/lines");

        // then
        지하철_노선_목록_조회_성공(lineCreateResponse01, lineCreateResponse02, lineGetResponse);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> lineCreateResponse = 지하철_노선_생성_요청_01(역_생성_요청_01(), 역_생성_요청_02(), 10);
        String expectLineId = lineCreateResponse.header("Location").split("/")[2];

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(expectLineId);

        // then
        지하철_노선_조회_성공(expectLineId, response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> lineCreateResponse = 지하철_노선_생성_요청_01(역_생성_요청_01(), 역_생성_요청_02(), 10);

        // when
        String updateLineId = lineCreateResponse.header("Location").split("/")[2];
        Map<String, String> params = new HashMap<>();
        params.put("name", "1호선");
        params.put("color", "#FF0000");
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(updateLineId, params);

        // then
        지하철_노선_수정_성공(params, response);
    }

    @DisplayName("지하철역을 제거하지 않고 지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> lineCreateResponse = 지하철_노선_생성_요청_01(역_생성_요청_01(), 역_생성_요청_02(), 10);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(lineCreateResponse.header("Location"));

        // then
        지하철_노선_제거_실패(response);
    }

    private void 지하철_노선_생성_성공(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_노선_생성_실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_노선_목록_조회_성공(ExtractableResponse<Response> lineCreateResponse01, ExtractableResponse<Response> lineCreateResponse02, ExtractableResponse<Response> lineGetResponse) {
        List<Long> expectedLineIds = Stream.of(lineCreateResponse01, lineCreateResponse02)
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> actualLineIds = lineGetResponse.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        assertThat(actualLineIds).containsAll(expectedLineIds);

        List<Station> expectedStations01 = lineCreateResponse01.jsonPath().get("stations");
        List<Station> expectedStations02 = lineCreateResponse02.jsonPath().get("stations");
        List<Station> actualStations = lineGetResponse.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getStations)
            .flatMap(List::stream)
            .collect(Collectors.toList());
        assertEquals(expectedStations01.size() + expectedStations02.size(), actualStations.size());
    }

    private void 지하철_노선_조회_성공(String expectLineId, ExtractableResponse<Response> response) {
        String actualLineId = response.jsonPath().get("id").toString();
        assertEquals(expectLineId, actualLineId);
    }

    private void 지하철_노선_수정_성공(Map<String, String> params, ExtractableResponse<Response> response) {
        assertEquals(params.get("color"), response.jsonPath().get("color"));
    }

    private void 지하철_노선_제거_성공(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void 지하철_노선_제거_실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
