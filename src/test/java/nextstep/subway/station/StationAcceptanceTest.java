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
    static final String URL_PATH_STAIONS = "/stations";
    static final String PARAM_NAME_TEXT = "name";

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
        String givenStationName = "강남역";
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_NAME_TEXT, givenStationName);

        ExtractableResponse<Response> response = createStation(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = getStationNames();
        assertThat(stationNames).containsAnyOf(givenStationName);
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
        params.put(PARAM_NAME_TEXT, "강남역");
        createStation(params);

        // when
        ExtractableResponse<Response> response = createStation(params);

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
        String[] givenStationNames = {"선릉역", "삼성역"};

        Map<String, String> params = new HashMap<>();
        for (String givenStationName : givenStationNames) {
            params.put(PARAM_NAME_TEXT, givenStationName);
            createStation(params);
        }

        // when
        List<String> stationNames = getStationNames();

        // then
        assertThat(stationNames).containsAnyOf(givenStationNames);
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
        String givenStationName = "삼성역";
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_NAME_TEXT, givenStationName);
        long id = createStation(params).jsonPath().getLong("id");

        // when
        RestAssured.given().log().all()
                .when().delete(URL_PATH_STAIONS + "/" + id)
                .then().log().all();

        // then
        List<String> stationNames = getStationNames();
        assertThat(stationNames).doesNotContain(givenStationName);
    }

    private List<String> getStationNames() {
        return RestAssured.given().log().all()
                .when().get(URL_PATH_STAIONS)
                .then().log().all()
                .extract().jsonPath().getList(PARAM_NAME_TEXT, String.class);
    }

    private ExtractableResponse<Response> createStation(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(URL_PATH_STAIONS)
                .then().log().all().extract();
    }
}
