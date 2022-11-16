package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;
import nextstep.subway.application.DatabaseCleanup;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @Autowired
    private StationRepository stationRepository;

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();
    }

    private Station saveStation(String name) {
        return stationRepository.save(new Station(name));
    }

    /*
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     * */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // When
        Station 강남역 = saveStation("강남역");
        Station 양재역 = saveStation("양재역");
        long lineId = createLine("신분당선", "bg-red-600", 강남역, 양재역, 10).extract().jsonPath().getLong("id");

        // Then
        JsonPath responseBody = fetchLines().extract().jsonPath();
        assertThat(responseBody.getList("id", Long.class)).containsAnyOf(lineId);
        assertThat(responseBody.getList("stations[0].name")).containsExactly(강남역.getName(), 양재역.getName());
    }

    /*
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     * */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // Given
        Station 강남역 = saveStation("강남역");
        Station 양재역 = saveStation("양재역");
        createLine("신분당선", "bg-red-600", 강남역, 양재역, 10).extract();

        Station 야탑역 = saveStation("야탑역");
        Station 모란역 = saveStation("모란역");
        createLine("분당선", "bg-yellow-600", 야탑역, 모란역, 10).extract();

        // When
        JsonPath responseBody = fetchLines().extract().jsonPath();

        // Then
        assertThat(responseBody.getList("").size()).isEqualTo(2);
    }

    /*
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     * */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // Given
        Station 강남역 = saveStation("강남역");
        Station 양재역 = saveStation("양재역");
        String lineName = "신분당선";
        long lineId = createLine(lineName, "bg-red-600", 강남역, 양재역, 10).extract().jsonPath().getLong("id");

        // When
        JsonPath responseBody = fetchLine(lineId).extract().jsonPath();

        // Then
        assertThat(responseBody.getLong("id")).isEqualTo(lineId);
        assertThat(responseBody.getString("name")).isEqualTo(lineName);
        assertThat(responseBody.getList("stations.name")).containsExactly(강남역.getName(), 양재역.getName());
    }

    /*
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     * */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // Given
        Station 강남역 = saveStation("강남역");
        Station 양재역 = saveStation("양재역");
        long lineId = createLine("신분당선", "bg-red-600", 강남역, 양재역, 10).extract().jsonPath().getLong("id");

        // When
        ValidatableResponse response = updateLine(lineId, "구분당선", "bg-blue-600");

        // Then
        assertThat(response.extract().statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /*
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     * */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        // Given
        Station 강남역 = saveStation("강남역");
        Station 양재역 = saveStation("양재역");
        long lineId = createLine("신분당선", "bg-red-600", 강남역, 양재역, 10).extract().jsonPath().getLong("id");

        // When
        ValidatableResponse response = deleteLine(lineId);

        // Then
        assertThat(response.extract().statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ValidatableResponse createLine(String name, String color, Station upStation, Station downStation, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStation.getId());
        params.put("downStationId", downStation.getId());
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all();
    }

    private ValidatableResponse fetchLines() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all();
    }

    private ValidatableResponse fetchLine(long lineId) {
        return RestAssured.given().log().all()
                .pathParam("id", lineId)
                .when().get("/lines/{id}")
                .then().log().all();
    }

    private ValidatableResponse updateLine(long lineId, String name, String color) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured.given().log().all()
                .body(params)
                .pathParam("id", lineId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{id}")
                .then().log().all();
    }

    private ValidatableResponse deleteLine(long lineId) {
        return RestAssured.given().log().all()
                .pathParam("id", lineId)
                .when().delete("/lines/{id}")
                .then().log().all();
    }
}
