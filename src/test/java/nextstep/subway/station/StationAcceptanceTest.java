package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.assertj.core.util.Maps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StationAcceptanceTest extends AcceptanceTest {
    private final static String API_URL_STATIONS = "/stations";
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
        ExtractableResponse<Response> response = registerStation("강남역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = findStationNames();
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
        registerStation("강남역");

        // when
        ExtractableResponse<Response> response = registerStation("강남역");

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
        registerStation("방배역");
        registerStation("서초역");

        // when
        List<String> stationNames = findStationNames();

        // then
        assertThat(stationNames).containsExactly("방배역", "서초역");

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
        String stationId = registerStation("사당역").jsonPath().getString("id");

        // when
        removeStation(stationId);
        List<String> stationNames = findStationNames();

        // then
        assertThat(stationNames.isEmpty() || !stationNames.contains("사당역")).isTrue();
    }

    private ExtractableResponse<Response> registerStation(String stationName) {
        return sendPost(Maps.newHashMap("name", stationName), API_URL_STATIONS);
    }

    private List<String> findStationNames() {
        return sendGet(API_URL_STATIONS).jsonPath().getList("name", String.class);
    }

    private ExtractableResponse<Response> removeStation(String stationId) {
        return sendDelete(API_URL_STATIONS + "/{id}", stationId);
    }
}
