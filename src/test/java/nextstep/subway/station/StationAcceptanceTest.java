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

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {
    static final String requestPath = "/stations";
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
        ExtractableResponse<Response> response = 지하철역_생성_요청("강남역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        ExtractableResponse<Response> getResponse = requestGetStations();
        List<String> stationNames = getResponse.jsonPath().getList("name", String.class);
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
        지하철역_생성_요청("강남역");

        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청("강남역");

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
        String[] stations = new String[]{"수락산역", "마들역"};
        for (String station : stations) {
            지하철역_생성_요청(station);
        }

        // when
        ExtractableResponse<Response> response = requestGetStations();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<String> findStations = response.jsonPath().getList("name", String.class);
        assertThat(findStations.size()).isEqualTo(stations.length);
        assertThat(findStations).contains(stations);
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
        String station = "수락산역";
        ExtractableResponse<Response> createdResponse = 지하철역_생성_요청(station);

        // when
        Long stationId = createdResponse.jsonPath().getLong("id");
        ExtractableResponse<Response> deleteResponse = requestDeleteStation(stationId);
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // then
        ExtractableResponse<Response> getResponse = requestGetStations();
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<String> findStations = getResponse.jsonPath().getList("name", String.class);
        assertThat(findStations.size()).isEqualTo(0);
        assertThat(findStations).doesNotContain(station);
    }

    private ExtractableResponse<Response> requestDeleteStation(Long stationId) {
        return RestAssured.given().log().all()
            .when().delete(String.format("/stations/%d", stationId))
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철역_생성_요청(String name) {
        return RestAssured.given().log().all()
            .body(new HashMap<String, String>() {
                {
                    put("name", name);
                }
            })
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post(requestPath)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> requestGetStations() {
        return RestAssured.given().log().all()
            .when().get(requestPath)
            .then().log().all()
            .extract();
    }
}
