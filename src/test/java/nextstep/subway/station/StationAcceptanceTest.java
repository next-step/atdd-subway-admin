package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
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
import static org.junit.jupiter.api.Assertions.assertAll;

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
        // when
        Map<String, String> params = new HashMap<>();
        params.put("name", "잠실역");

        ExtractableResponse<Response> response = insertStation(params).extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames =
                selectStations().extract().jsonPath().getList("name", String.class);
        assertThat(stationNames).containsAnyOf("잠실역");
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
        params.put("name", "강남역");

        // when
        ExtractableResponse<Response> response = insertStation(params).extract();

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
        // given: 2개의 지하철역이 등록되어 있다.
        Map<String, String> param = new HashMap<>();

        param.put("name", "강남역");
        assertThat(insertStation(param).extract().statusCode()).isEqualTo(HttpStatus.CREATED.value());

        param.put("name", "홍대역");
        assertThat(insertStation(param).extract().statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when: 지하철역의 리스트 정보를 조회한다.
        ExtractableResponse<Response> response = selectStations().extract();

        // then: 등록되어있는 지하철역의 리스트 정보를 응답 받는다.
        List<String> stationNames =
                response.response().body().jsonPath().getList("name", String.class);
        assertAll (
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(stationNames.contains("강남역")).isTrue(),
                () -> assertThat(stationNames.contains("홍대역")).isTrue(),
                () -> assertThat(stationNames.size()).isEqualTo(2)
        );
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given: 제거할 지하철 역을 생성한다.
        Map<String, String> param = new HashMap<>();

        param.put("name", "강남역");
        ExtractableResponse<Response> created = insertStation(param).extract();
        assertThat(created.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when: 생성한 지하철을 삭제한다.
        ExtractableResponse<Response> response = deleteStation(null, created.header("Location")).extract();

        // then: 정상적으로 삭제처리가 되어야 한다.
        ExtractableResponse<Response> deleted = selectStations().extract();

        List<String> stationNames =
                deleted.response().body().jsonPath().getList("name", String.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(stationNames.contains("강남역")).isFalse()
        );
    }

    /**
     * When 존재하지 않는 지하철을 삭제하면
     * Then 삭제처리 되지 않아야 한다.
     */
    @DisplayName("존재하지 않은 지하철역 제거한다.")
    @Test
    void deleteStationNotFoundStation() {
        // when: 존재하지 않는 지하철을 삭제한다.
        ExtractableResponse<Response> response = deleteStation(1L, null).extract();

        // then: 정상적으로 삭제되지 않아야 한다.
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private ValidatableResponse insertStation(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all();
    }

    private ValidatableResponse selectStations() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all();
    }

    private ValidatableResponse deleteStation(Long id, String location) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(generateDeleteUrl(id, location))
                .then().log().all();
    }

    private String generateDeleteUrl(Long id, String location) {
        if (location != null) {
            return location;
        }
        return "/stations/" + id;
    }

}
