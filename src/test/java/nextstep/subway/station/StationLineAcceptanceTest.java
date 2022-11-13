package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 노선 인수 테스트")
class StationLineAcceptanceTest extends AcceptanceTest {
    private Long upStationId;
    private Long downStationId;

    @BeforeEach
    private void before() {
        upStationId = requestCreateStation("강남역");
        downStationId = requestCreateStation("성수역");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    @DisplayName("지하철 노선 생성")
    void createStationLine() {
        ExtractableResponse<Response> response = requestCreateStationLine("신분당선");
        assertThat(response.statusCode()).isEqualTo(200);

        List<String> lineNames = getList(requestGetStationLines(), "name");
        assertThat(lineNames).contains("신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    @DisplayName("지하철 노선 목록 조회")
    void getStationLines() {
        requestCreateStationLine("신분당선");
        requestCreateStationLine("분당선");

        ExtractableResponse<Response> response = requestGetStationLines();
        assertThat(response.statusCode()).isEqualTo(200);

        List<String> lineNames = getList(response, "name");
        assertThat(lineNames).contains("신분당선", "분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    @DisplayName("지하철 노선 조회")
    void getStationLine() {
        Long lineId = requestCreateStationLine("신분당선").jsonPath().getLong("id");

        ExtractableResponse<Response> response = requestGetStationLine(lineId);
        assertThat(response.statusCode()).isEqualTo(200);

        String name = response.jsonPath().getString("name");
        assertThat(name).isEqualTo("신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    @DisplayName("지하철 노선 수정")
    void updateStationLine() {
        Long lineId = requestCreateStationLine("신분당선").jsonPath().getLong("id");

        Map<String, Object> updateParams = new HashMap<>();
        updateParams.put("name", "분당선");
        updateParams.put("color", "bg-red-600");

        ExtractableResponse<Response> response = requestUpdateStationLine(lineId, updateParams);
        assertThat(response.statusCode()).isEqualTo(200);

        String lineName = requestGetStationLine(lineId).jsonPath().getString("name");
        String color = requestGetStationLine(lineId).jsonPath().getString("color");
        assertThat(lineName).isEqualTo("분당선");
        assertThat(color).isEqualTo("bg-red-600");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    @DisplayName("지하철 노선 삭제")
    void deleteStationLine() {
        Long lineId = requestCreateStationLine("신분당선").jsonPath().getLong("id");

        ExtractableResponse<Response> response = requestDeleteStationLine(lineId);
        assertThat(response.statusCode()).isEqualTo(200);

        List<String> lineNames = getList(requestGetStationLines(), "name");
        assertThat(lineNames).isEmpty();
    }

    private ExtractableResponse<Response> requestDeleteStationLine(Long lineId) {
        return RestAssured
                .given().log().all()
                .when().delete("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();
    }

    private List<String> getList(ExtractableResponse<Response> response, String key) {
        return response.jsonPath().getList(key, String.class);
    }

    private ExtractableResponse<Response> requestCreateStationLine(String lineName) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", lineName);
        params.put("color", "bg-red-600");
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", 10);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> requestUpdateStationLine(long stationLineId, Map<String, Object> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{stationLineId}", stationLineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> requestGetStationLines() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> requestGetStationLine(long stationLineId) {
        return RestAssured.given().log().all()
                .when().get("/lines/{stationLineId}", stationLineId)
                .then().log().all().extract();
    }

    private Long requestCreateStation(final String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract()
                .jsonPath()
                .getLong("id");
    }
}
