package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.DatabaseInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.station.StationAcceptanceMethods.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StationAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    private DatabaseInitializer databaseInitializer;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseInitializer.afterPropertiesSet();
        }

        databaseInitializer.initialize();
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
        ExtractableResponse<Response> createStationResponse = 지하철역_생성("논현역");

        // then
        assertThat(createStationResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        ExtractableResponse<Response> showStationsResponse = 지하철역_목록_조회();
        List<String> stationNames = showStationsResponse.jsonPath().getList("name", String.class);
        assertThat(stationNames).containsAnyOf("논현역");
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
        지하철역_생성("강남역");

        // when
        ExtractableResponse<Response> response = 지하철역_생성("강남역");

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
        지하철역_생성("강남역");
        지하철역_생성("역삼역");

        ExtractableResponse<Response> response = 지하철역_목록_조회();

        JsonPath jsonPath = response.body().jsonPath();
        assertAll(
                () -> assertThat(jsonPath.getList("id")).hasSize(2),
                () -> assertThat(jsonPath.getList("name")).containsExactlyInAnyOrder("강남역", "역삼역")
        );
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        ExtractableResponse<Response> createStationResponse = 지하철역_생성("교대역");
        long id = createStationResponse.jsonPath().getLong("id");

        StationAcceptanceMethods.지하철역_삭제(id);

        ExtractableResponse<Response> showStationsResponse = 지하철역_목록_조회();

        JsonPath jsonPath = showStationsResponse.body().jsonPath();
        assertAll(
                () -> assertThat(jsonPath.getList("id")).doesNotContain(id),
                () -> assertThat(jsonPath.getList("name")).doesNotContain("교대역")
        );

    }
}
