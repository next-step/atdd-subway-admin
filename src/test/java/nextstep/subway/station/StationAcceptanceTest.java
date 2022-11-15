package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
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

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ValidatableResponse response = createStation("강남역");

        // then
        assertThat(response.extract().statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = fetchStation().extract()
                .jsonPath()
                .getList("name", String.class);
        assertThat(stationNames).containsAnyOf("강남역");
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        createStation("강남역");

        // when
        ValidatableResponse response = createStation("강남역");

        // then
        assertThat(response.extract().statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        createStation("강남역");
        createStation("양재역");

        // when
        List<String> stationNames = fetchStation()
                .extract()
                .jsonPath()
                .getList("name", String.class);

        // then
        assertThat(stationNames).containsExactly("강남역", "양재역");
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        long stationId = createStation("강남역").extract()
                .jsonPath()
                .getLong("id");

        // when
        int statusCode = deleteStation(stationId).extract()
                .response()
                .statusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.NO_CONTENT.value());

        // then
        List<String> stationNames = fetchStation().extract()
                .jsonPath()
                .getList("name", String.class);
        assertThat(stationNames).isEmpty();
    }

    /*
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     * */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        createStation("강남역");
        createStation("양재역");
        List<Long> stationsIds = fetchStation()
                .extract()
                .jsonPath()
                .getList("id", Long.class);

        // When
        ExtractableResponse<Response> response = createLine("신분당선", "bg-red-600", stationsIds.get(0), stationsIds.get(1), 10).extract();

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        JsonPath responseBody = response.jsonPath();
        System.out.println(responseBody.toString());
    }

    /*
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     * */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {

    }

    /*
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     * */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {

    }

    /*
    * Given 지하철 노선을 생성하고
    * When 생성한 지하철 노선을 수정하면
    * Then 해당 지하철 노선 정보는 수정된다
    * */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {

    }

    /*
    * Given 지하철 노선을 생성하고
    * When 생성한 지하철 노선을 삭제하면
    * Then 해당 지하철 노선 정보는 삭제된다
    * */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {

    }

    private ValidatableResponse createLine(String name, String color, long upStationId, long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all();
    }

    private ValidatableResponse createStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all();
    }

    private ValidatableResponse deleteStation(long id) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .when().delete("/stations/{id}")
                .then().log().all();
    }

    private ValidatableResponse fetchStation() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all();
    }
}
