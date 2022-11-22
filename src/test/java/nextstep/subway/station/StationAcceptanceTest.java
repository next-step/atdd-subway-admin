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
    public static final String STATION_URL = "/stations";
    public static final String STATION_KEY_NAME = "name";
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
        Map<String, String> params = new HashMap<>();
        params.put(STATION_KEY_NAME, "강남역");

        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post(STATION_URL)
                        .then().log().all()
                        .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames =
                RestAssured.given().log().all()
                        .when().get(STATION_URL)
                        .then().log().all()
                        .extract().jsonPath().getList(STATION_KEY_NAME, String.class);
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
        Map<String, String> params = new HashMap<>();
        params.put(STATION_KEY_NAME, "강남역");

        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(STATION_URL)
                .then().log().all();

        // when
        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post(STATION_URL)
                        .then().log().all()
                        .extract();

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
        Map<String, String> StationsParam = new HashMap<>();
        StationsParam.put(STATION_KEY_NAME, "잠실역");

        RestAssured.given().log().all()
                .body(StationsParam)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(STATION_URL)
                .then().log().all();

        Map<String, String> StationParams = new HashMap<>();
        StationParams.put(STATION_KEY_NAME, "양재역");

        RestAssured.given().log().all()
                .body(StationParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(STATION_URL)
                .then().log().all();

        // when
        List<String> stationNames =
                RestAssured.given().log().all()
                        .when().get(STATION_URL)
                        .then().log().all()
                        .extract().jsonPath().getList(STATION_KEY_NAME, String.class);


        // then
        assertThat(stationNames.size()).isEqualTo(2);
        assertThat(stationNames).contains("잠실역","양재역");
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
        Map<String, String> StationsParam = new HashMap<>();
        StationsParam.put(STATION_KEY_NAME, "강남역");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(StationsParam)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(STATION_URL)
                .then().log().all()
                .extract();

        // when
        Long id = response.jsonPath().getLong("id");
        RestAssured.given().log().all()
                .when().delete(STATION_URL+"/{id}",id)
                .then().log().all();

        // then
        List<String> stationNames =
                RestAssured.given().log().all()
                        .when().get(STATION_URL)
                        .then().log().all()
                        .extract().jsonPath().getList(STATION_KEY_NAME, String.class);
        assertThat(stationNames).doesNotContain("강남역");

    }
}
