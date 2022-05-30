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
    static final String URL_PATH_STATIONS = "/stations";
    static final String KEY_NAME = "name";

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
        String testStationName = "강남역";
        ExtractableResponse<Response> response = createStation(testStationName);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        assertThat(getStationNames()).containsAnyOf(testStationName);
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
        String testStationName = "강남역";
        createStation(testStationName);

        // when
        ExtractableResponse<Response> response = createStation(testStationName);

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
        String[] testStationNames = {"선릉역", "삼성역"};
        createStation(testStationNames[0]);
        createStation(testStationNames[1]);

        // when
        List<String> stationNames = getStationNames();

        // then
        assertThat(stationNames).containsAnyOf(testStationNames);
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
        String testStationName = "삼성역";
        long stationId = createStation(testStationName).jsonPath().getLong("id");

        // when
        assertThat(deleteStation(stationId).statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // then
        assertThat(getStationNames()).doesNotContain(testStationName);
    }

    private List<String> getStationNames() {
        return RestAssured.given().log().all()
                .when().get(URL_PATH_STATIONS)
                .then().log().all()
                .extract().jsonPath().getList(KEY_NAME, String.class);
    }

    private ExtractableResponse<Response> createStation(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put(KEY_NAME, stationName);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(URL_PATH_STATIONS)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> deleteStation(long stationId) {
        return RestAssured.given().log().all()
                .when().delete(URL_PATH_STATIONS + "/" + stationId)
                .then().log().all().extract();
    }
}
