package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.application.DatabaseCleanup;
import nextstep.subway.dto.StationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("acceptance")
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
        ExtractableResponse<Response> response = Station.createStation("강남역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = findStations()
                .jsonPath().getList("name", String.class);
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
        StationRequest request = Station.createStationRequest("강남역");
        Station.createStation(request);

        // when
        ExtractableResponse<Response> response = Station.createStation(request);

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
        Station.createStation("강남역");
        Station.createStation("잠실역");

        // when
        List<String> stationNames = findStations()
                .jsonPath().getList("name", String.class);

        // then
        assertThat(stationNames).containsExactlyInAnyOrder("강남역", "잠실역");
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
        ExtractableResponse<Response> response = Station.createStation("강남역");
        int id = response.jsonPath().get("id");

        // when
        deleteStation(id);

        // then
        List<String> names = findStations().jsonPath().getList("name");
        assertThat(names).isEmpty();
    }

    private ExtractableResponse<Response> deleteStation(int id) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .when().delete("/stations/{id}")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> findStations() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract();
    }
}
