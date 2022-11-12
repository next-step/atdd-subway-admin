package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
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
        String stationName = "강남역";
        ExtractableResponse<Response> response = createStationWithName(stationName);

        // then
        checkStatusCode(response.statusCode(), HttpStatus.CREATED.value());

        List<String> allStationNames = getAllStationNames("name");
        checkContainName(allStationNames, stationName);

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
        String stationName = "강남역";
        createStationWithName(stationName);

        // when
        String duplicatedName = "강남역";
        ExtractableResponse<Response> response = createStationWithName(duplicatedName);

        // then
        checkStatusCode(response.statusCode(), HttpStatus.BAD_REQUEST.value());
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
        createStationWithName("강남역");
        createStationWithName("역삼역");

        // when
        ExtractableResponse<Response> response = getAllStations();

        // then
        assertThat(response.jsonPath().getList("name", String.class)).hasSize(2);
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
        String name = "강남역";
        ExtractableResponse<Response> response = createStationWithName(name);
        int id = response.jsonPath().getInt("id");

        // when
        deleteStationById(id);

        // then
        assertThat(getStationById(id).statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());

    }

    /**
     * 특정 지하철역 조회
     * @param id
     * @return
     */
    private ExtractableResponse<Response> getStationById(int id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/stations/" + id)
                .then().log().all()
                .extract();
    }

    /**
     * 아이디로 지하철역 제거
     * @param id
     * @return
     */
    private ExtractableResponse<Response> deleteStationById(int id) {
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/stations/" + id)
                .then().log().all()
                .extract();
    }


    /**
     * 지하철역 생성
     *
     * @param stationName
     * @return
     */
    private ExtractableResponse<Response> createStationWithName(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        return RestAssured.given()
                .body(params).log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then()
                .log().all()
                .extract();
    }

    /**
     * 지하철역 전체 조회
     * @return
     */
    private ExtractableResponse<Response> getAllStations() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/stations")
                .then().log().all()
                .extract();
    }

    /**
     * 상태 코드 체크
     *
     * @param statusCode
     * @param value
     */
    private static void checkStatusCode(int statusCode, int value) {
        assertThat(statusCode).isEqualTo(value);
    }

    /**
     * 모든 역 이름으로 조회
     *
     * @param target
     * @return
     */
    private List<String> getAllStationNames(String target) {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract().jsonPath().getList(target, String.class);
    }


    /**
     * 역 이름이 존재하는가
     *
     * @param allStationNames
     * @param stationName
     */
    private void checkContainName(List<String> allStationNames, String stationName) {
        assertThat(allStationNames).containsAnyOf(stationName);
    }
}
