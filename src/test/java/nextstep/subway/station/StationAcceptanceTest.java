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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {
    private final String STATION_PATH = "/stations";

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
        ExtractableResponse<Response> response = postRequest(STATION_PATH, new HashMap<>(), Collections.singletonMap("name", "강남역"));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = getRequest(STATION_PATH, new HashMap<>())
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
        postRequest(STATION_PATH, new HashMap<>(), Collections.singletonMap("name", "강남역"));

        // when
        ExtractableResponse<Response> response = postRequest(STATION_PATH, new HashMap<>(), Collections.singletonMap("name", "강남역"));

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
        postRequest(STATION_PATH, new HashMap<>(), Collections.singletonMap("name", "강남역"));
        postRequest(STATION_PATH, new HashMap<>(), Collections.singletonMap("name", "삼성역"));

        // when
        List<String> stationNames = getRequest(STATION_PATH, new HashMap<>())
                .jsonPath()
                .getList("name", String.class);

        // then
        assertThat(stationNames).containsAnyOf("강남역", "삼성역");
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
        String createdStationId = postRequest(STATION_PATH, new HashMap<>(), Collections.singletonMap("name", "강남역"))
                .body()
                .jsonPath()
                .get("id")
                .toString();

        // when
        ExtractableResponse<Response> deleteResponse = deleteRequest(STATION_PATH + "/{id}", new HashMap<>(), createdStationId);
        List<String> stationIds = getRequest(STATION_PATH, new HashMap<>())
                .jsonPath()
                .getList("id", String.class);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(stationIds).doesNotContain(createdStationId);
    }

    private ExtractableResponse<Response> getRequest(
            String path, Map<String, String> params, Object... pathVariables
    ) {
        return RestAssured.given().log().all()
                .params(params)
                .when().get(path, pathVariables)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> postRequest(
            String path, Map<String, String> params, Map<String, String> body, Object... pathVariables
    ) {
        return RestAssured.given().log().all()
                .params(params)
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(path, pathVariables)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> deleteRequest(
            String path, Map<String, String> params, Object... pathVariables
    ) {
        return RestAssured.given().log().all()
                .params(params)
                .when().delete(path, pathVariables)
                .then().log().all()
                .extract();
    }
}
