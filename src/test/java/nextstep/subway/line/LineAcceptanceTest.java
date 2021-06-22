package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    public static LineResponse 지하철노선_등록(String name, String color, Long upStationId, Long downStationId, int instance) {
        Map<String, String> params = generateLineParam(name,color, upStationId, downStationId, instance);
        ExtractableResponse<Response> response = saveLine(params);
        return response.jsonPath().getObject(".", LineResponse.class);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {

        // given
        StationResponse upStation = StationAcceptanceTest.지하철역_등록("강남역");
        StationResponse downStation = StationAcceptanceTest.지하철역_등록("교대역");
        Map<String, String> params = generateLineParam("2호선","green", upStation.getId(), downStation.getId(), 1000);

        // when
        ExtractableResponse<Response> response = saveLine(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }


    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        StationResponse upStation = StationAcceptanceTest.지하철역_등록("강남역");
        StationResponse downStation = StationAcceptanceTest.지하철역_등록("교대역");
        Map<String, String> params = generateLineParam("2호선","green", upStation.getId(), downStation.getId(), 1000);
        saveLine(params);

        // when
        ExtractableResponse<Response> response = saveLine(params);

        // then
        // 생성 실패
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());

    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        StationResponse upStation = StationAcceptanceTest.지하철역_등록("강남역");
        StationResponse downStation = StationAcceptanceTest.지하철역_등록("교대역");
        Map<String, String> params = generateLineParam("2호선","green", upStation.getId(), downStation.getId(), 1000);
        saveLine(params);

        // when
        ExtractableResponse<Response> response = searchLines();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<String> names = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(lineResponse -> lineResponse.getName())
                .collect(Collectors.toList());
        assertThat(names).contains(params.get("name"));

        LineResponse lineResponseList = response.jsonPath().getList(".", LineResponse.class).get(0);
        assertThat(lineResponseList.getStations().get(0).getName()).isEqualTo(upStation.getName());
        assertThat(lineResponseList.getStations().get(1).getName()).isEqualTo(downStation.getName());
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        StationResponse upStation = StationAcceptanceTest.지하철역_등록("강남역");
        StationResponse downStation = StationAcceptanceTest.지하철역_등록("교대역");
        Map<String, String> params = generateLineParam("2호선","green", upStation.getId(), downStation.getId(), 1000);
        ExtractableResponse<Response> expect = saveLine(params);
        long savedId = convertToId(expect.header("Location"));

        // when
        ExtractableResponse<Response> response = searchLine(savedId);
        LineResponse searchedLine = response.jsonPath().getObject(".", LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(params.get("name")).isEqualTo(searchedLine.getName());
    }

    private long convertToId(String location) {
        return Long.valueOf(location.replaceAll("/lines/",""));

    }

    @DisplayName("지하철 노선 조회 실패")
    @Test
    void getLineWithWrongId() {
        // given
        Map<String, String> params = generateLineParam("2호선","green", 1l, 2l, 1000);
        saveLine(params);

        // when
        ExtractableResponse<Response> response = searchLine(0L);

        // then
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }


    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        StationResponse upStation = StationAcceptanceTest.지하철역_등록("강남역");
        StationResponse downStation = StationAcceptanceTest.지하철역_등록("교대역");
        Map<String, String> params = generateLineParam("2호선","green", upStation.getId(), downStation.getId(), 1000);
        ExtractableResponse<Response> expect = saveLine(params);
        long savedId = convertToId(expect.header("Location"));
        LineResponse savedLine = expect.jsonPath().getObject(".", LineResponse.class);
        Map<String, String> updatedParam = generateLineParam("3호선","orange",upStation.getId(), downStation.getId(), 1000);

        // when
        ExtractableResponse<Response> response = editLine(savedId, updatedParam);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> updatedResponse = searchLine(savedLine.getId());
        LineResponse updatedLine = updatedResponse.jsonPath().getObject(".", LineResponse.class);
        assertThat(updatedLine.getName()).isEqualTo(updatedParam.get("name"));
        assertThat(updatedLine.getColor()).isEqualTo(updatedParam.get("color"));
    }


    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        StationResponse upStation = StationAcceptanceTest.지하철역_등록("강남역");
        StationResponse downStation = StationAcceptanceTest.지하철역_등록("교대역");
        Map<String, String> params = generateLineParam("2호선","green", upStation.getId(), downStation.getId(), 1000);
        ExtractableResponse<Response> expect = saveLine(params);
        long savedId = convertToId(expect.header("Location"));

        // when
        ExtractableResponse<Response> response = removeLine(savedId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> removeLine(Long lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/"+lineId)
                .then().log().all()
                .extract();
    }


    public static Map<String, String> generateLineParam(String name, String color, long upStationId, long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        params.put("distance", String.valueOf(distance));
        return params;
    }

    private static ExtractableResponse<Response> saveLine(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> searchLines() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> searchLine(long lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/"+lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> editLine(long lineId, Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/"+lineId)
                .then().log().all()
                .extract();
    }



}
