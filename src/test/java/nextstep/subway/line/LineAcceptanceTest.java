package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nextstep.subway.station.StationAcceptanceTest.createNewStation;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = createNewLine(createLineParams());

        // then
        // 지하철_노선_생성됨
        assertResponseHttpStatusIsCreate(response);
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicationName() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = createLineParams();
        createNewLine(params);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = createNewLine(params);

        // then
        // 지하철_노선_생성_실패됨
        assertResponseHttpStatusIsBadRequest(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse1 = createNewLine(createLineParams());
        // 지하철_노선_등록되어_있음
        Map<String, String> params = new LineParameter()
                .name("2호선")
                .color("bg-green-600")
                .upStationId(createStationId("이대"))
                .downStationId(createStationId("신촌"))
                .distance("10")
                .getMap();
        ExtractableResponse<Response> createResponse2 = createNewLine(params);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_목록_응답됨
        assertResponseHttpStatusIsOk(response);
        // 지하철_노선_목록_포함됨
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = createNewLine(createLineParams());

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get(createResponse.header("Location"))
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_응답됨
        assertResponseHttpStatusIsOk(response);
        // 지하철_노선_역목록_확인
        List<Object> stations = response.jsonPath().getList("stations");
        assertThat(stations.size()).isEqualTo(2);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = createNewLine(createLineParams());

        // when
        // 지하철_노선_수정_요청
        Map<String, String> params = new LineParameter()
                .name("구분당선")
                .color("bg-blue-600")
                .distance("10")
                .getMap();
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(uri)
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_수정됨
        assertResponseHttpStatusIsOk(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = createNewLine(createLineParams());

        // when
        // 지하철_노선_제거_요청
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_삭제됨
        assertResponseHttpStatusIsNoContent(response);
    }

    @DisplayName("상행역과 하행역을 똑같이 하여 노선을 생성한다.")
    @Test
    void createLineWithSameUpDownStation() {
        // given
        // 상행역_하행역_일치
        String stationId = createStationId("이대");
        Map<String, String> params = new LineParameter()
                .name("2호선")
                .color("bg-green-600")
                .upStationId(stationId)
                .downStationId(stationId)
                .distance("10")
                .getMap();

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = createNewLine(params);

        // then
        // 지하철_노선_생성_실패됨
        assertResponseHttpStatusIsBadRequest(response);
    }

    @DisplayName("잘못된 노선 이름으로 생성한다.")
    @Test
    void createLineWithInvalidName() {
        // given
        Map<String, String> params = new LineParameter()
                .name("   ")
                .color("bg-red-600")
                .upStationId(createStationId("강남"))
                .downStationId(createStationId("광교"))
                .distance("10")
                .getMap();

        // when
        ExtractableResponse<Response> response = createNewLine(params);

        // then
        assertResponseHttpStatusIsBadRequest(response);
    }

    @DisplayName("잘못된 노선 색상으로 생성한다.")
    @Test
    void createLineWithInvalidColor() {
        // given
        Map<String, String> params = new LineParameter()
                .name("신분당선")
                .color("   ")
                .upStationId(createStationId("강남"))
                .downStationId(createStationId("광교"))
                .distance("10")
                .getMap();

        // when
        ExtractableResponse<Response> response = createNewLine(params);

        // then
        assertResponseHttpStatusIsBadRequest(response);
    }

    @DisplayName("잘못된 노선 거리로 생성한다.")
    @Test
    void createLineWithInvalidDistance() {
        // given
        Map<String, String> params = new LineParameter()
                .name("신분당선")
                .color("bg-red-600")
                .upStationId(createStationId("강남"))
                .downStationId(createStationId("광교"))
                .distance("0")
                .getMap();

        // when
        ExtractableResponse<Response> response = createNewLine(params);

        // then
        assertResponseHttpStatusIsBadRequest(response);
    }

    @DisplayName("존재하지 않는 노선 역으로 생성한다.")
    @Test
    void createLineWithInvalidStation() {
        // given
        Map<String, String> params = new LineParameter()
                .name("신분당선")
                .color("bg-red-600")
                .upStationId("100")
                .downStationId("100")
                .distance("10")
                .getMap();

        // when
        ExtractableResponse<Response> response = createNewLine(params);

        // then
        assertResponseHttpStatusIsNotFound(response);
    }

    private Map<String, String> createLineParams() {
        return new LineParameter()
                .name("신분당선")
                .color("bg-red-600")
                .upStationId(createStationId("강남"))
                .downStationId(createStationId("광교"))
                .distance("10")
                .getMap();
    }

    private String createStationId(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        ExtractableResponse<Response> response = createNewStation(params);
        return String.valueOf(response.jsonPath()
            .getObject(".", StationResponse.class)
            .getId());
    }

    private ExtractableResponse<Response> createNewLine(Map<String, String> params) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    public static class LineParameter {

        private final Map<String, String> map = new HashMap<>();

        public LineParameter name(String name) {
            map.put("name", name);
            return this;
        }

        public LineParameter color(String color) {
            map.put("color", color);
            return this;
        }

        public LineParameter upStationId(String upStationId) {
            map.put("upStationId", upStationId);
            return this;
        }

        public LineParameter downStationId(String downStationId) {
            map.put("downStationId", downStationId);
            return this;
        }

        public LineParameter distance(String distance) {
            map.put("distance", distance);
            return this;
        }

        public Map<String, String> getMap() {
            return map;
        }
    }
}
