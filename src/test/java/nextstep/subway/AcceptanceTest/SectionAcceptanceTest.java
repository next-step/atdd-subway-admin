package nextstep.subway.AcceptanceTest;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.DatabaseCleaner;
import nextstep.subway.api.LineApi;
import nextstep.subway.api.StationApi;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SectionAcceptanceTest {
    private final LineApi lineApi = new LineApi();
    private final StationApi stationApi = new StationApi();

    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleaner databaseCleaner;

    Long 광교역, 강남역;
    Long 신분당선;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }

        광교역 = stationApi.createId("광교역");
        강남역 = stationApi.createId("강남역");
        신분당선 = lineApi.create("신분당선", "bg-red-600", 강남역, 광교역).jsonPath().getLong("id");
    }

    @AfterEach
    void cleanUp() {
        databaseCleaner.execute();
    }

    /**
     * When 지하철 노선에 새로운 지하철역 등록을 요청한다.
     * Then 지하철 노선에 지하철역이 등록된다.
     */
    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void create() {
        // when
        Long 양재역 = stationApi.createId("양재역");
        ExtractableResponse<Response> response = lineApi.addSection(신분당선, 강남역, 양재역, 5);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(lineApi.findById(신분당선).jsonPath().getList("stations.name")).contains("양재역")
        );
    }

    /**
     * Given 하행역을 상행 종점역과 동일하게 입력한다.
     * When 지하철 노선에 지하철역 등록을 요청한다.
     * Then 지하철 노선에 지하철역이 등록된다.
     * Then 지하철 노선의 상행 종점역이 새로운 역으로 변경된다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void createNewEndUpStation() {
        // given
        Long 신논현역 = stationApi.createId("신논현역");

        // when
        ExtractableResponse<Response> response = lineApi.addSection(신분당선, 신논현역, 강남역, 5);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(getLineStations(신분당선).get(0).getName()).isEqualTo("신논현역");
    }

    /**
     * Given 상행역을 하행 종점역과 동일하게 입력한다.
     * When 지하철 노선에 지하철역 등록을 요청한다.
     * Then 지하철 노선에 지하철역이 등록된다.
     * Then 지하철 노선의 하행 종점역이 새로운 역으로 변경된다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void createNewEndDownStation() {
        // given
        Long 호매실역 = stationApi.createId("호매실역");

        // when
        ExtractableResponse<Response> response = lineApi.addSection(신분당선, 광교역, 호매실역, 5);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(getLineStations(신분당선).get(lineApi.findNames().size() - 1).getName()).isEqualTo("호매실역");
    }

    private List<Station> getLineStations(Long id) {
        return lineApi.findById(신분당선).jsonPath().getList("stations");
    }
}
