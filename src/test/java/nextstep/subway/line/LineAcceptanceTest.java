package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    private final StationAcceptanceTest stationAcceptanceTest = new StationAcceptanceTest();

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /*
    * When 지하철 노선을 생성하면
    * Then 지하철 노선이 생성된다
    * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
    */
    @Test
    void 지하철노선_생성() {
        // when
        ExtractableResponse<Response> response = createLine("신분당선", "bg-red-600", 10, "강남역", "역삼역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /*
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    void 지하철노선_목록_조회() {
        // given
        ExtractableResponse<Response> station1 = stationAcceptanceTest.createStation("지하철역");
        ExtractableResponse<Response> station2 = stationAcceptanceTest.createStation("새로운지하철역");
        ExtractableResponse<Response> station3 = stationAcceptanceTest.createStation("또다른지하철역");

        createLine("신분당선", "bg-red-600", 10, station1.jsonPath().getLong("id"), station2.jsonPath().getLong("id"));
        createLine("분당선", "bg-green-600", 10, station1.jsonPath().getLong("id"), station3.jsonPath().getLong("id"));

        // when
        ExtractableResponse<Response> response = findAllLines();
        List<String> lineNames = response.jsonPath().getList("name");

        // then
        assertThat(lineNames).contains("신분당선", "분당선");
    }

    /*
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    void 지하철노선_단건_조회() {
        // given
        stationAcceptanceTest.createStation("지하철역");
        stationAcceptanceTest.createStation("새로운지하철역");

        ExtractableResponse<Response> line = createLine("신분당선", "bg-red-600", 10, "지하철역", "새로운지하철역");

        // when
        ExtractableResponse<Response> response = findLineById(line.jsonPath().getLong("id"));

        // then
        assertThat(response.jsonPath().getLong("id")).isEqualTo(line.jsonPath().getLong("id"));
    }

    /*
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    void 지하철노선_수정() {
        // given
        ExtractableResponse<Response> resultLine = createLine("신분당선", "bg-red-600", 10, "지하철역", "새로운지하철역");

        // when
        ExtractableResponse<Response> response = modifyLine(resultLine.jsonPath().getLong("id"), "다른분당선", "bg-red-600");
        ExtractableResponse<Response> findLine = findLineById(resultLine.jsonPath().getLong("id"));

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(findLine.jsonPath().getString("name")).isEqualTo("다른분당선"),
                () -> assertThat(findLine.jsonPath().getString("color")).isEqualTo("bg-red-600")
        );
    }

    /*
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    void 지하철노선_삭제() {
        // given
        ExtractableResponse<Response> line = createLine("신분당선", "bg-red-600", 10, "지하철역", "새로운지하철역");

        // when
        ExtractableResponse<Response> response = deleteLineById(line.jsonPath().getLong("id"));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> createLine(String name, String color, int distance, String upStationName, String downStationName) {
        ExtractableResponse<Response> upStation = stationAcceptanceTest.createStation(upStationName);
        ExtractableResponse<Response> downStation = stationAcceptanceTest.createStation(downStationName);

        Map<String, Object> param = new HashMap<>();
        param.put("name", name);
        param.put("color", color);
        param.put("upStationId", upStation.jsonPath().getLong("id"));
        param.put("downStationId", downStation.jsonPath().getLong("id"));
        param.put("distance", distance);

        return RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createLine(String name, String color, int distance, Long upStationId, Long downStationId) {
        Map<String, Object> param = new HashMap<>();
        param.put("name", name);
        param.put("color", color);
        param.put("upStationId", upStationId);
        param.put("downStationId", downStationId);
        param.put("distance", distance);

        return RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> findAllLines() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> findLineById(Long id) {
        return RestAssured.given().log().all()
                .when().get("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> modifyLine(Long id, String name, String color) {
        Map<String, Object> param = new HashMap<>();
        param.put("name", name);
        param.put("color", color);

        return RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> deleteLineById(Long id) {
        return RestAssured.given().log().all()
                .when().delete("/lines/{id}", id)
                .then().log().all()
                .extract();
    }
}
