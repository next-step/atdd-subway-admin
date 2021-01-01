package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private StationResponse 강남역;
    private StationResponse 광교역;
    private ExtractableResponse<Response> response;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        // given
        강남역 = StationAcceptanceTest.지하철_등록_요청("강남역").as(StationResponse.class);
        광교역 = StationAcceptanceTest.지하철_등록_요청("광교역").as(StationResponse.class);
        Map<String, String> createParams = new HashMap<>();
        createParams.put("name", "신분당선");
        createParams.put("color", "bg-red-600");
        createParams.put("upStationId", 강남역.getId() + "");
        createParams.put("downStationId", 광교역.getId() + "");
        createParams.put("distance", 10 + "");
        response = LineAcceptanceTest.지하철_노선_생성_요청(createParams);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        LineResponse 신분당선 = response.as(LineResponse.class);
        assertThat(신분당선.getName()).isEqualTo("신분당선");
        assertThat(신분당선.getStations().get(0).getId()).isEqualTo(강남역.getId());
        assertThat(신분당선.getStations().get(1).getId()).isEqualTo(광교역.getId());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        StationResponse 삼성역 = StationAcceptanceTest.지하철_등록_요청("삼성역").as(StationResponse.class);
        StationResponse 잠실역 = StationAcceptanceTest.지하철_등록_요청("잠실역").as(StationResponse.class);
        Map<String, String> createParams = new HashMap<>();
        createParams.put("name", "2호선");
        createParams.put("color", "bg-green-600");
        createParams.put("upStationId", 삼성역.getId() + "");
        createParams.put("downStationId", 잠실역.getId() + "");
        createParams.put("distance", 10 + "");
        ExtractableResponse<Response> response2 = LineAcceptanceTest.지하철_노선_생성_요청(createParams);

        // when
        ExtractableResponse<Response> listResponse = 지하철_노선_목록_조회_요청();

        // then
        assertThat(listResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(response, response2).stream()
            .map(line -> getCreatedLineId(line))
            .collect(Collectors.toList());
        List<Long> actualLineIds = listResponse.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        assertThat(actualLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // when
        Long lineId = getCreatedLineId(response);
        ExtractableResponse<Response> lineResponse = 지하철_노선_조회_요청(lineId);
        LineResponse 신분당선 = lineResponse.body().as(LineResponse.class);

        // then
        assertThat(lineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(신분당선.getName()).isEqualTo("신분당선");
        assertThat(신분당선.getColor()).isEqualTo("bg-red-600");
        assertThat(신분당선.getStations().size()).isEqualTo(2);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        Map<String, String> params = 지하철_노선_생성_파라미터("2호선", "green");
        ExtractableResponse<Response> createdLine = 지하철_노선_생성_요청(params);

        // when
        Map<String, String> params2 = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "bluegreen");
        Long lineId = getCreatedLineId(createdLine);
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(params2, lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        Map<String, String> params = 지하철_노선_생성_파라미터("2호선", "green");
        ExtractableResponse<Response> createdLine = 지하철_노선_생성_요청(params);

        // when
        Long lineId = getCreatedLineId(createdLine);
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private Map<String, String> 지하철_노선_생성_파라미터(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return params;
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, String> params) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(Long lineId) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines/" + lineId)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(Map<String, String> params2, Long lineId) {
        return RestAssured.given().log().all()
            .body(params2)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/" + lineId)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(Long lineId) {
        return RestAssured.given().log().all()
            .when()
            .delete("/lines/" + lineId)
            .then().log().all()
            .extract();
    }

    private long getCreatedLineId(ExtractableResponse<Response> createdLine) {
        return Long.parseLong(createdLine.header("Location").split("/")[2]);
    }
}
