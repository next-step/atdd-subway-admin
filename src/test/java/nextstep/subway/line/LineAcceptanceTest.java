package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.station.StationAcceptanceTest.createTestStation;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@Sql("/truncate.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineAcceptanceTest {
    @LocalServerPort
    int port;

    private Long firstStationId;
    private Long secondStationId;
    private Long thirdStationId;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        firstStationId = createTestStation("지하철역").jsonPath().getLong("id");
        secondStationId = createTestStation("새로운지하철역").jsonPath().getLong("id");
        thirdStationId = createTestStation("또다른지하철역").jsonPath().getLong("id");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선 생성")
    @Test
    void createLineTest() {
        // when
        createLine("신분당선", "bg-red-600", 10L, firstStationId, secondStationId);

        // then
        List<Object> lineNames = getLines()
                .jsonPath().getList("name");

        assertThat(lineNames).containsAnyOf("신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록 조회")
    @Test
    void getLinesTest() {
        // Given
        createLine("신분당선", "bg-red-600", 10L, firstStationId, secondStationId);
        createLine("분당선", "bg-green-600", 15L, firstStationId, thirdStationId);

        // When
        List<Object> lineNames = getLines().jsonPath().getList("name");

        // Then
        assertThat(lineNames).containsExactly("신분당선", "분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선 조회")
    @Test
    void getLineTest() {
        // Given
        Long lineId = createLine("신분당선", "bg-red-600", 10L, firstStationId, secondStationId)
                .jsonPath().getLong("id");

        // When
        String name = getLine(lineId).jsonPath().getString("name");

        // Then
        assertThat(name).isEqualTo("신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선 수정")
    @Test
    void updateLineTest() {
        // Given
        Long lineId = createLine("신분당선", "bg-red-600", 10L, firstStationId, thirdStationId)
                .jsonPath().getLong("id");

        // When
        updateLine(lineId, "다른분당선", "bg-red-600", null, null, null);

        // Then
        String name = getLine(lineId).jsonPath().get("name");

        assertThat(name).isEqualTo("다른분당선");
    }

    private Map<String, String> createLineRequestMap(String name, String color, Long distance, Long upStationId, Long downStationId) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        if (distance != null) {
            params.put("distance", String.valueOf(distance));
        }
        if (upStationId != null) {
            params.put("upStationId", String.valueOf(upStationId));
        }
        if (downStationId != null) {
            params.put("downStationId", String.valueOf(downStationId));
        }

        return params;
    }

    private ExtractableResponse<Response> createLine(String name, String color, Long distance, Long upStationId, Long downStationId) {
        Map<String, String> params = createLineRequestMap(name, color, distance, upStationId, downStationId);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> getLines() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> getLine(Long id) {
        return RestAssured.given().log().all()
                .when().get("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> updateLine(Long id, String name, String color, Long distance, Long upStationId, Long downStationId) {
        Map<String, String> params = createLineRequestMap(name, color, distance, upStationId, downStationId);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{id}", id)
                .then().log().all()
                .extract();
    }
}
