package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        //when
        ExtractableResponse<Response> response = createStation("강남역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = getAllStation().jsonPath().getList("name", String.class);
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
        createStation("강남역");

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
        //given
        createStation("새로운지하철역이름");
        createStation("또다른지하철역이름");

        //when
        List<String> allStationNames = getAllStation().jsonPath().getList("name", String.class);

        // then
        assertThat(allStationNames).containsExactlyInAnyOrder("새로운지하철역이름", "또다른지하철역이름");
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        //given
        String createdStationId = createStation("강남역").jsonPath().getString("id");

        //when
        assertThat(deleteStation(createdStationId).statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        //then
        List<String> allStationNames = getAllStation().jsonPath().getList("name", String.class);
        assertThat(allStationNames).doesNotContain("강남역");
    }

    private ExtractableResponse<Response> createStation(String station) {
        Map<String, String> params = new HashMap<>();
        params.put("name", station);

        return RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("/stations")
                        .then().log().all()
                        .extract();
    }

    private ExtractableResponse<Response> deleteStation(String id) {
        return RestAssured.given().log().all()
                        .when().delete("/stations/{id}", id)
                        .then().log().all()
                        .extract();
    }

    private ExtractableResponse<Response> getAllStation() {
        return RestAssured
                .given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract();
    }
}
