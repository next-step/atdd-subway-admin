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
        Map<String, String> params = 지하철_노선_생성_파라미터("2호선", "green");

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        Map<String, String> params = 지하철_노선_생성_파라미터("2호선", "green");
        지하철_노선_생성_요청(params);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        Map<String, String> params = 지하철_노선_생성_파라미터("2호선", "green");
        ExtractableResponse<Response> createdLine1 = 지하철_노선_생성_요청(params);
        Map<String, String> params2 = 지하철_노선_생성_파라미터("8호선", "pink");
        ExtractableResponse<Response> createdLine2 = 지하철_노선_생성_요청(params2);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createdLine1, createdLine2).stream()
            .map(line -> getCreatedLineId(line))
            .collect(Collectors.toList());
        List<Long> actualLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        assertThat(actualLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        String name = "2호선";
        String color = "green";
        Map<String, String> params = 지하철_노선_생성_파라미터(name, color);
        ExtractableResponse<Response> createdLine = 지하철_노선_생성_요청(params);

        // when
        Long lineId = getCreatedLineId(createdLine);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines/" + lineId)
            .then().log().all()
            .extract();
        LineResponse lineResponse = response.body().as(LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.getName()).isEqualTo(name);
        assertThat(lineResponse.getColor()).isEqualTo(color);
    }

    @DisplayName("지하철 노선을 조회 시, 상행역과 하행역을 함께 조회한다.")
    @Test
    void getLineWithUpDownStation() {
        // given
        Map<String, String> param1 = new HashMap<>();
        param1.put("name", "강남역");
        ExtractableResponse<Response> 강남역 = StationAcceptanceTest.지하철_등록_요청(param1);
        Map<String, String> param2 = new HashMap<>();
        param2.put("name", "양재역");
        ExtractableResponse<Response> 양재역 = StationAcceptanceTest.지하철_등록_요청(param2);

        String name = "신분당선";
        String color = "red";
        Map<String, String> params = 지하철_노선_생성_파라미터_WITH_상행_하행(name, color,
            강남역.body().as(StationResponse.class).getId(), 양재역.body().as(StationResponse.class).getId());
        ExtractableResponse<Response> createdLine = 지하철_노선_생성_요청(params);

        // when
        // 지하철_노선_조회_요청
        Long lineId = getCreatedLineId(createdLine);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines/" + lineId)
            .then().log().all()
            .extract();
        LineResponse lineResponse = response.body().as(LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.getName()).isEqualTo(name);
        assertThat(lineResponse.getColor()).isEqualTo(color);
        assertThat(lineResponse.getStations().get(0).getName).isEqualTo(param1.get("name"));
        assertThat(lineResponse.getStations().get(1).getName).isEqualTo(param2.get("name"));
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

    private Map<String, String> 지하철_노선_생성_파라미터_WITH_상행_하행(String name, String color, Long upStationId, Long downStationId) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        params.put("downStationId", "10");
        return params;
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, String> params) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
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
