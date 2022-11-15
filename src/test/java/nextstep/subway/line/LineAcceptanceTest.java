package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
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
    private StationRepository stationRepository;

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    private long getStationId(String name) {
        return stationRepository.save(new Station(name)).getId();
    }

    /*
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     * */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // When
        ExtractableResponse<Response> response = createLine("신분당선", "bg-red-600", getStationId("강남역"), getStationId("양재역"), 10).extract();

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
        // Given
        createLine("신분당선", "bg-red-600", getStationId("강남역"), getStationId("양재역"), 10).extract();
        createLine("분당선", "bg-yellow-600", getStationId("야탑역"), getStationId("모란역"), 10).extract();
        JsonPath jsonPath = fetchLines().extract().jsonPath();
        System.out.println(jsonPath);
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

    private ValidatableResponse fetchLines() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all();
    }
}
