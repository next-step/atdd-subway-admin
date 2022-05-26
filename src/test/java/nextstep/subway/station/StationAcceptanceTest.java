package nextstep.subway.station;

import com.jayway.jsonpath.JsonPath;
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
import java.util.Optional;

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
    void createStationTest() {
        // when
        ExtractableResponse<Response> station = createStation("송파역");

        // then
        assertThat(station.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = fetchAllStationName();
        assertThat(stationNames).containsAnyOf("송파역");
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
        ExtractableResponse<Response> duplicateStation = createStation("강남역");

        // then
        assertThat(duplicateStation.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        //given
        ExtractableResponse<Response> station = createStation("금천역");
        ExtractableResponse<Response> station2 = createStation("구로역");

        //when
        List<String> stationList = fetchAllStationName();

        //then
        assertThat(stationList).contains("금천역");
        assertThat(stationList).contains("구로역");
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStationTest() {
        //given
        ExtractableResponse<Response> station = createStation("마포역");

        //when
        int stationId = station.jsonPath().get("id");
        deleteStation(stationId);

        //then
        Optional<Map<String,?>> deleted = fetchStationBy("마포역");
        assertThat(deleted).isEmpty();
    }

    private ExtractableResponse<Response> createStation(String name) {
        //when
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
        .body(params)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when().post("/stations")
        .then().log().all()
        .extract();
    }

    private List<String> fetchAllStationName() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
    }

    private void deleteStation(int id) {
        RestAssured.given().log().all()
                .pathParam("id", id).log().all()
                .when().delete("/stations/{id}")
                .then().log().all()
                .extract();
    }

    private Optional<Map<String,?>> fetchStationBy(String name) {
        List<Map<String,?>> jsonAsArrayList = RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract().jsonPath().get();

        return jsonAsArrayList.stream()
                .filter(m -> m.get("name").equals(name)).findFirst();
    }
}
