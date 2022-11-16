package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.station.StationAcceptanceTestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
    void 지하철역생성_성공() {
        // when
        String stationName = "강남역";
        ExtractableResponse<Response> response = createStation(stationName);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = getStations()
                .jsonPath()
                .getList("name", String.class);
        assertThat(stationNames).containsAnyOf(stationName);
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void 지하철역생성_실패_역이름중복() {
        // given
        String stationName = "강남역";
        createStation(stationName);

        // when
        ExtractableResponse<Response> response = createStation(stationName);

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
    void 지하철역조회_성공() {
        // given
        String gangNamStationName = "강남역";
        String suwonStationName = "수원역";
        createStation(gangNamStationName);
        createStation(suwonStationName);

        // when
        List<String> stationNames = getStations()
                .jsonPath()
                .getList("name", String.class);

        // then
        assertAll(
                () -> assertThat(stationNames).containsAnyOf(gangNamStationName),
                () -> assertThat(stationNames).containsAnyOf(suwonStationName)
        );
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void 지하철역삭제_성공() {
        // given
        String stationName = "강남역";
        Long id = createStation(stationName).jsonPath().getLong("id");

        // when
        deleteStation(id);

        // then
        List<String> stationNames = getStations()
                .jsonPath()
                .getList("name", String.class);

        assertThat(stationNames).doesNotContain(stationName);
    }
}
