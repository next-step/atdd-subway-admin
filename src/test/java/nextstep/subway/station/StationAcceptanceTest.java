package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import nextstep.subway.application.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();
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
