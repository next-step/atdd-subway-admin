package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.domain.PathConstant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest extends BaseAcceptanceTest {

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void givenCreateStation() {
        // when
        ExtractableResponse<Response> response = givenCreateStation("강남역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        assertThat(thenGetStations()).containsAnyOf("강남역");
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
        givenCreateStation("강남역");

        // when
        ExtractableResponse<Response> response = givenCreateStation("강남역");

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
        givenCreateStation("강남역");
        givenCreateStation("양재역");

        // when
        List<String> stationNames = thenGetStations();

        // then
        assertThat(stationNames).containsAnyOf("강남역", "양재역");
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
        ExtractableResponse<Response> createdResponse = givenCreateStation("강남역");

        // when
        whenDeleteStation(getStationId(createdResponse));

        // then
        assertThat(thenGetStation("강남역")).isEmpty();
    }

    private long getStationId(ExtractableResponse<Response> createdResponse) {
        JsonPath jsonPath = createdResponse.jsonPath();
        return jsonPath.getLong("id");
    }

    public static ExtractableResponse<Response> givenCreateStation(String name){
        return RestAssured.given().log().all()
                .body(createParam(name))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(PathConstant.STATION_ROOT_PATH)
                .then().log().all()
                .extract();
    }

    private static List<String> thenGetStation(String name) {
        return RestAssured.given().log().all()
                .body(createParam(name))
                .when().get(PathConstant.STATION_ROOT_PATH)
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
    }

    private static List<String> thenGetStations() {
        return RestAssured.given().log().all()
                .when().get(PathConstant.STATION_ROOT_PATH)
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
    }

    private void whenDeleteStation(long id) {
        RestAssured.given().log().all()
                .when().delete(PathConstant.STATION_ROOT_PATH + PathConstant.PATH_SEPARATOR + id)
                .then().log().all();
    }

    private static HashMap<Object, Object> createParam(String name) {
        HashMap<Object, Object> params = new HashMap<>();
        params.put("name", name);
        return params;
    }

}
