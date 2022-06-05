package nextstep.subway.linestation;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.line.LineAcceptanceTest.createLine;
import static nextstep.subway.line.LineAcceptanceTest.getLine;
import static nextstep.subway.station.StationAcceptanceTest.createTestStation;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 구간 관련 기능")
@Sql("/truncate.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineStationAcceptanceTests {
    @LocalServerPort
    int port;

    private List<Long> stationIds;
    private Long upStationId;
    private Long downStationId;
    private Long newStationId;
    private Long lineId;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        upStationId = createTestStation("지하철역").jsonPath().getLong("id");
        downStationId = createTestStation("새로운지하철역").jsonPath().getLong("id");
        lineId = createLine("신분당선", "bg-red-600", 10L, upStationId, downStationId)
                .jsonPath().getLong("id");
        newStationId = createTestStation("또다른지하철역").jsonPath().getLong("id");
        stationIds = Arrays.asList(upStationId, downStationId, newStationId);
    }

    /**
     * Given 새로운 역을 구간등록 히면
     * Then 해당 지하철 노선을 조회할 때 추가된 노선을 확인할 수 있다.
     */
    @DisplayName("노선 추가")
    @ParameterizedTest
    @CsvSource(value = {"0,2,0,2,1", "2,0,2,0,1", "1,2,0,1,2"})
    // middle 추가, 상행 종점 추가, 하행 종점 추가
    void createLineStationTest(int newUpStationId, int newDownStationId, int firstId, int secondId, int thirdId) {
        // Given
        createLineStation(lineId, 5L, stationIds.get(newUpStationId), stationIds.get(newDownStationId));

        // Then
        List<Long> result = getLine(lineId).jsonPath().getList("stations.id", Long.class);
        assertThat(result).containsExactly(stationIds.get(firstId), stationIds.get(secondId), stationIds.get(thirdId));
    }

    /**
     * Given 새로운 역을 구간등록 하면
     * Then 등록될 수 없다
     */
    @DisplayName("노선 추가시 거리가 크거나 같으면 등록할 수 없다")
    @ParameterizedTest
    @ValueSource(longs = {10L, 11L})
    void validateDistance_createLineStation(Long distance) {
        // Given
        int statusCode = createLineStation(lineId, distance, upStationId, newStationId).statusCode();

        // Then
        assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 새로운 역을 구간등록 하면
     * Then 등록될 수 없다
     */
    @DisplayName("상,하행선이 이미 노선에 모두 등록되어 있으면 등록할 수 없다")
    @Test
    void validateDuplicate_createLineStation() {
        // Given
        int statusCode = createLineStation(lineId, 5L, upStationId, downStationId).statusCode();

        // Then
        assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 신규 역을 등록하고
     * Given 새로운 역을 구간등록 하면
     * Then 등록될 수 없다
     */
    @DisplayName("상,하행선이 모두 노선에 없으면 등록할 수 없다")
    @Test
    void validateNoMatch_createLineStation() {
        // Given
        Long otherStationId = createTestStation("신규지하철역").jsonPath().getLong("id");

        // Given
        int statusCode = createLineStation(lineId, 5L, newStationId, otherStationId).statusCode();

        // Then
        assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 추가 구간을 등록하고
     * When 구간을 제거하면
     * Then 해당 지하철 노선을 조회할 때 제거된 것을 확인할 수 있다.
     */
    @DisplayName("구간을 제거할 수 있다")
    @ParameterizedTest
    @CsvSource(value = {"0,1,2", "1,0,2", "2,0,1"})
    // 상행 종점 삭제, middle 삭제, 하행 종점 삭제
    void deleteLineStationTest(int stationId, int firstStationId, int secondStationId) {
        // Given
        createLineStation(lineId, 5L, downStationId, newStationId);

        // When
        deleteLineStation(lineId, stationIds.get(stationId));

        // Then
        List<Long> result = getLine(lineId).jsonPath().getList("stations.id", Long.class);
        assertThat(result).containsExactly(stationIds.get(firstStationId), stationIds.get(secondStationId));
    }

    /**
     * When 하나만 있는 구간을 제거하면
     * Then 제거할 수 없다.
     */
    @DisplayName("구간이 하나인 노선에서 마지막 구간을 제거할 수 없다")
    @Test
    void validateLastLineStation_deleteLineStation() {
        // When
        int statusCode = deleteLineStation(lineId, upStationId).statusCode();

        // Then
        assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 추가 구간을 등록하고
     * Given 추가 역을 등록하고
     * When 추가된 역을 제거하면
     * Then 제거할 수 없다.
     */
    @DisplayName("노선에 없는 역은 제거할 수 없다")
    @Test
    void validateNonMatch_deleteLineStation() {
        // Given
        createLineStation(lineId, 5L, downStationId, newStationId);

        // Given
        long newStationId = createTestStation("신규지하철역").jsonPath().getLong("id");

        // When
        int statusCode = deleteLineStation(lineId, newStationId).statusCode();

        // Then
        assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> deleteLineStation(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("stationId", stationId)
                .when().delete("/lines/{id}/stations", lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createLineStation(Long lineId, Long distance, Long upStationId, Long downStationId) {
        Map<String, String> params = createStationRequestMap(distance, upStationId, downStationId);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{id}/stations", lineId)
                .then().log().all()
                .extract();
    }

    private Map<String, String> createStationRequestMap(Long distance, Long upStationId, Long downStationId) {
        Map<String, String> params = new HashMap<>();
        params.put("distance", String.valueOf(distance));
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));

        return params;
    }
}
