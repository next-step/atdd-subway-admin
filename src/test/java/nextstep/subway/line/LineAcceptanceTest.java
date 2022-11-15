package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.utils.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends BaseTest {
    @Autowired
    StationRepository stationRepository;

    @Autowired
    LineRepository lineRepository;

    private void 역_생성(){
        stationRepository.save(new Station("서울대입구역"));
        stationRepository.save(new Station("낙성대역"));
        stationRepository.save(new Station("시청역"));
        stationRepository.save(new Station("영등포역"));
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선 생성")
    @Test
    void 지하철_노선_생성() {
        역_생성();
        ExtractableResponse<Response> response = createLine("2호선", "bg-green-800", 1, 2, 500);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        List<String> lineNames = getLines();
        assertThat(lineNames).containsAnyOf("2호선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록 조회")
    @Test
    void 지하철노선_목록_조회() {
        역_생성();
        createLine("1호선", "bg-blue-200", 3, 4, 400);
        createLine("2호선", "bg-green-800", 1, 2, 500);

        List<String> lineNames = getLines();

        assertThat(lineNames.size()).isEqualTo(2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선 조회")
    @Test
    void 지하철노선_조회() {
        역_생성();
        Long lineId = createLine("2호선", "bg-green-800", 1, 2, 500).jsonPath().getLong("id");

        ExtractableResponse<Response> response = getLine(lineId);

        assertThat(response.jsonPath().getLong("id")).isEqualTo(1L);
        assertThat(response.jsonPath().getString("name")).isEqualTo("2호선");
        assertThat(response.jsonPath().getString("color")).isEqualTo("bg-green-800");
        assertThat(response.jsonPath().getString("stations[0].name")).isEqualTo("서울대입구역");
        assertThat(response.jsonPath().getString("stations[1].name")).isEqualTo("낙성대역");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선 수정")
    @Test
    void 지하철노선_수정() {
        역_생성();
        Long lineId = createLine("2호선", "bg-green-800", 1, 2, 500).jsonPath().getLong("id");

        updateLine(lineId, "3호선", "bg-orange-900");

        ExtractableResponse<Response> response = getLine(lineId);
        assertThat(response.jsonPath().getString("name")).isEqualTo("3호선");
        assertThat(response.jsonPath().getString("color")).isEqualTo("bg-orange-900");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선 삭제")
    @Test
    void 지하철노선_삭제() {
        역_생성();
        Long lineId = createLine("2호선", "bg-green-800", 1, 2, 500).jsonPath().getLong("id");

        deleteLine(lineId);

        assertThat(lineRepository.findById(lineId)).isEmpty();
    }

    private static ExtractableResponse<Response> getLine(Long lineId) {
        return RestAssured.given().log().all()
                .when().get("/lines/" + lineId)
                .then().log().all()
                .extract();
    }

    private static List<String> getLines() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
    }

    private static ExtractableResponse<Response> createLine(String name, String color, int upStationId, int downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> updateLine(Long lineId, String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/" + lineId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    private static ExtractableResponse<Response> deleteLine(Long lineId) {
        return RestAssured.given().log().all()
                .when().delete("/lines/" + lineId)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract();
    }
}
