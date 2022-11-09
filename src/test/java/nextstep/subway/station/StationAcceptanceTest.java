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
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class StationAcceptanceTest {
    private static final String STATION_NAME = "name";
    private static final String STATION_MAIN_PATH = "/stations";
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
        ExtractableResponse<Response> saveResponse = createStation("강남역");

        // then
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        ExtractableResponse<Response> findResponse = findAllStation();
        assertThat(findResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(covertResponseToStationNames(findResponse)).containsAnyOf("강남역");
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
        ExtractableResponse<Response> saveResponse = createStation("강남역");
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        ExtractableResponse<Response> response = createStation("강남역");

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
        ExtractableResponse<Response> gangNamResponse = createStation("강남역");
        assertThat(gangNamResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        ExtractableResponse<Response> seongSuResponse = createStation("성수역");
        assertThat(seongSuResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        //when
        ExtractableResponse<Response> findResponse = findAllStation();

        //then
        assertThat(findResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<String> stations = covertResponseToStationNames(findResponse);
        assertThat(stations).hasSize(2);
        assertThat(stations).containsExactly("강남역", "성수역");
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
    }

    private ExtractableResponse<Response> createStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put(STATION_NAME, name);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(STATION_MAIN_PATH)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> findAllStation() {
        return RestAssured.given().log().all()
                .when().get(STATION_MAIN_PATH)
                .then().log().all()
                .extract();
    }

    private List<String> covertResponseToStationNames(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(STATION_NAME, String.class);
    }
}
