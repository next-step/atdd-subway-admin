package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
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

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        // 지하철_역_등록되어_있음
        ExtractableResponse<Response> gangnamStation = StationAcceptanceTest.createStation("강남역");
        ExtractableResponse<Response> gwanggyoStation = StationAcceptanceTest.createStation("광교역");

        // when
        // 지하철_노선_생성_요청
        Long gangnamStationId = Long.parseLong(getUri(gangnamStation).split("/")[2]);
        Long gwanggyoStationId = Long.parseLong(getUri(gwanggyoStation).split("/")[2]);
        ExtractableResponse<Response> response = createLine(new LineRequest("신분당선", "bg-red-600", gangnamStationId, gwanggyoStationId, 10));

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> gangnamStation = StationAcceptanceTest.createStation("강남역");
        ExtractableResponse<Response> gwanggyoStation = StationAcceptanceTest.createStation("광교역");
        Long gangnamStationId = Long.parseLong(getUri(gangnamStation).split("/")[2]);
        Long gwanggyoStationId = Long.parseLong(getUri(gwanggyoStation).split("/")[2]);
        createLine(new LineRequest("신분당선", "bg-red-600", gangnamStationId, gwanggyoStationId, 10));

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = createLine(new LineRequest("신분당선", "bg-red-600", gangnamStationId, gwanggyoStationId, 10));

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 신분당선_지하철_역_등록되어_있음
        ExtractableResponse<Response> gangnamStation = StationAcceptanceTest.createStation("강남역");
        ExtractableResponse<Response> gwanggyoStation = StationAcceptanceTest.createStation("광교역");
        // 신분당선_노선_등록되어_있음
        Long gangnamStationId = Long.parseLong(getUri(gangnamStation).split("/")[2]);
        Long gwanggyoStationId = Long.parseLong(getUri(gwanggyoStation).split("/")[2]);
        ExtractableResponse<Response> shinbundangLine = createLine(new LineRequest("신분당선", "bg-red-600", gangnamStationId, gwanggyoStationId, 10));
        // 1호선_지하철_역_등록되어_있음
        ExtractableResponse<Response> soyosanStation = StationAcceptanceTest.createStation("소요산역");
        ExtractableResponse<Response> incheonStation = StationAcceptanceTest.createStation("인천역");
        // 1호선_노선_등록되어_있음
        Long soyosanStationId = Long.parseLong(getUri(soyosanStation).split("/")[2]);
        Long incheonStationId = Long.parseLong(getUri(incheonStation).split("/")[2]);
        ExtractableResponse<Response> lineOne = createLine(new LineRequest("1호선", "blue", soyosanStationId, incheonStationId, 20));

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all().extract();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_조회됨
        List<Long> expectedLineIds = Arrays.asList(shinbundangLine, lineOne).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_역_등록되어_있음
        ExtractableResponse<Response> gangnamStation = StationAcceptanceTest.createStation("강남역");
        ExtractableResponse<Response> gwanggyoStation = StationAcceptanceTest.createStation("광교역");
        // 지하철_노선_등록되어_있음
        Long gangnamStationId = Long.parseLong(getUri(gangnamStation).split("/")[2]);
        Long gwanggyoStationId = Long.parseLong(getUri(gwanggyoStation).split("/")[2]);
        ExtractableResponse<Response> createResponse = createLine(new LineRequest("신분당선", "bg-red-600", gangnamStationId, gwanggyoStationId, 10));

        // when
        // 지하철_노선_조회_요청
        String uri = getUri(createResponse);
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get(uri)
                .then().log().all().extract();

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_조회됨
        Long expectedLineId = Long.parseLong(uri.split("/")[2]);
        List<Long> expectedStationIds = Arrays.asList(gangnamStationId, gwanggyoStationId);
        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
        Long resultLineId = lineResponse.getId();
        List<Long> resultStationIds = lineResponse.getStations().stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineId).isEqualTo(expectedLineId);
        assertThat(resultStationIds).containsAll(expectedStationIds);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> gangnamStation = StationAcceptanceTest.createStation("강남역");
        ExtractableResponse<Response> gwanggyoStation = StationAcceptanceTest.createStation("광교역");
        Long gangnamStationId = Long.parseLong(getUri(gangnamStation).split("/")[2]);
        Long gwanggyoStationId = Long.parseLong(getUri(gwanggyoStation).split("/")[2]);
        ExtractableResponse<Response> createResponse = createLine(new LineRequest("신분당선", "bg-red-600", gangnamStationId, gwanggyoStationId, 10));

        // when
        // 지하철_노선_수정_요청
        String uri = getUri(createResponse);
        Map<String, String> params = new HashMap<>();
        params.put("name", "1호선");
        params.put("color", "blue");
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(uri)
                .then().log().all().extract();

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> gangnamStation = StationAcceptanceTest.createStation("강남역");
        ExtractableResponse<Response> gwanggyoStation = StationAcceptanceTest.createStation("광교역");
        Long gangnamStationId = Long.parseLong(getUri(gangnamStation).split("/")[2]);
        Long gwanggyoStationId = Long.parseLong(getUri(gwanggyoStation).split("/")[2]);
        ExtractableResponse<Response> createResponse = createLine(new LineRequest("신분당선", "bg-red-600", gangnamStationId, gwanggyoStationId, 10));

        // when
        // 지하철_노선_제거_요청
        String uri = getUri(createResponse);
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().delete(uri)
                .then().log().all().extract();

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> createLine(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> createLine(LineRequest lineRequest) {
        Map<String, String> params = new HashMap<>();
        params.put("name", lineRequest.getName());
        params.put("color", lineRequest.getColor());
        params.put("upStationId", lineRequest.getUpStationId().toString());
        params.put("downStationId", lineRequest.getDownStationId().toString());
        params.put("distance", String.valueOf(lineRequest.getDistance()));
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    private String getUri(ExtractableResponse<Response> response) {
        return response.header("Location");
    }
}
