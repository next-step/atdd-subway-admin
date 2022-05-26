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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {
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
        // given
        String name = "사당역";

        // when
        ExtractableResponse<Response> response = createStation(name);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = getStationNames();
        assertThat(stationNames).containsAnyOf(name);
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
        String name = "강남역";
        createStation(name);

        // when
        ExtractableResponse<Response> response = createStation(name);

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
        List<String> names = Arrays.asList("강남역", "서울역");

        for (String name : names) {
            // when
            ExtractableResponse<Response> response = createStation(name);
            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        }

        // then
        List<String> stationNames = getStationNames();
        assertThat(stationNames).containsExactly("강남역", "서울역");
        assertThat(stationNames).hasSize(names.size());
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
        String name = "사당역";

        ExtractableResponse<Response> created = createStation(name);
        assertThat(created.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        ExtractableResponse<Response> deleted = deleteStation(created.jsonPath().getLong("id"));
        assertThat(deleted.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // then
        List<String> stationNames = getStationNames();
        assertThat(stationNames.contains(name)).isFalse();
    }

    ExtractableResponse<Response> createStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                          .body(params)
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().post("/stations")
                          .then().log().all()
                          .extract();
    }

    ExtractableResponse<Response> deleteStation(Long stationId) {
        return RestAssured.given().log().all()
                          .when().delete("/stations/{id}", stationId)
                          .then().log().all()
                          .extract();
    }

    List<String> getStationNames() {
        return RestAssured.given().log().all()
                          .when().get("/stations")
                          .then().log().all()
                          .extract().jsonPath().getList("name", String.class);
    }
}
