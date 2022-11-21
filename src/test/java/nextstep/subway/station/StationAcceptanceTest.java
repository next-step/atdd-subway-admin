package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import nextstep.subway.AbstractAcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.AcceptanceUtils.assertStatusCode;
import static nextstep.subway.AcceptanceUtils.extractNames;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AbstractAcceptanceTest {
    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void create_station() {
        // when
        ValidatableResponse response = createStation("강남역");

        // then
        assertStatusCode(response, HttpStatus.CREATED);

        // then
        assertThat(extractNames(requestApiByGetStations())).containsAnyOf("강남역");
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void create_station_with_duplicate_name() {
        // given
        String stationName = "강남역";
        createStation(stationName);

        // when
        ValidatableResponse response = createStation(stationName);

        // then
        assertStatusCode(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void get_stations() {
        // given
        createStation("강남역");
        createStation("양재역");

        // when
        ValidatableResponse response = requestApiByGetStations();

        // then
        assertStatusCode(response, HttpStatus.OK);
        assertThat(extractNames(response)).containsAnyOf("강남역", "양재역");
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void delete_station() {
        // given
        long id = createStation("강남역").extract()
            .jsonPath().getLong("id");

        // when
        ValidatableResponse response = requestApiByDeleteStation(id);

        // then
        assertStatusCode(response, HttpStatus.NO_CONTENT);

        // then
        assertThat(extractNames(requestApiByGetStations())).doesNotContain("강남역");

    }

    private static ValidatableResponse requestApiByDeleteStation(long id) {
        return RestAssured.given().log().all()
            .when().delete("/stations/{id}", id)
            .then().log().all();
    }

    private static ValidatableResponse requestApiByGetStations() {
        return RestAssured.given().log().all()
            .when().get("/stations")
            .then().log().all();
    }

    public static ValidatableResponse createStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/stations")
            .then().log().all();
    }

}
