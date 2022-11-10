package nextstep.subway.station;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@Sql("/truncate.sql")
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
        String name = "강남역";
        ExtractableResponse<Response> response = createStations(name);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = fetchStations().jsonPath().getList("name", String.class);
        assertThat(stationNames).containsAnyOf(name);
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
        String name = "강남역";
        createStations(name);

        // when
        ExtractableResponse<Response> response = createStations(name);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
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
        List<String> names = Arrays.asList("강남역", "역삼역");
        createStations(names.get(0));
        createStations(names.get(1));

        // when
        JsonPath responseJson = fetchStations().jsonPath();

        // then
        assertThat(responseJson.getList("name", String.class)).containsAll(names);
        assertThat(responseJson.getList("id", Long.class)).hasSize(2);
        assertThat(responseJson.getList("createdDate", String.class)).hasSize(2);
        assertThat(responseJson.getList("modifiedDate", String.class)).hasSize(2);
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
        List<String> names = Arrays.asList("강남역", "역삼역");
        String deletedName = names.get(0);
        createStations(deletedName);
        createStations(names.get(1));
        Long id = fetchStations().jsonPath().getList("id", Long.class).get(0);

        // when
        // 강남역 제거
        ExtractableResponse<Response> response = deleteStations(id);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        List<String> stationsNames = fetchStations().jsonPath().getList("name", String.class);
        assertThat(stationsNames).doesNotContain(deletedName);
    }

    private ExtractableResponse<Response> deleteStations(Long id) {
        return RestAssured.given().log().all()
            .when().delete("/stations/" + id)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> createStations(String name) {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        return RestAssured.given().log().all()
            .body(map)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/stations")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> fetchStations() {
        return RestAssured.given().log().all()
            .when().get("/stations")
            .then().log().all()
            .extract();
    }
}
