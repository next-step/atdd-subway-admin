package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.StationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

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
        // when
        ValidatableResponse createResponse = 지하철역_등록("강남역");

        // then
        응답_검증(createResponse, HttpStatus.CREATED);

        // then
        List<Station> stations = 지하철역_목록_조회();
        지하철역_등록_검증(stations, "강남역");
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
        지하철역_등록("강남역");

        // when
        ValidatableResponse response = 지하철역_등록("강남역");

        // then
        응답_검증(response, HttpStatus.BAD_REQUEST);
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
        지하철역_등록("잠실역");
        지하철역_등록("강남역");

        // when
        List<Station> stations = 지하철역_목록_조회();
        지하철역_개수_검증(stations, 2);
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
        ValidatableResponse response = 지하철역_등록("강남역");
        long createdStationId = 응답_속성_조회(response, "id");

        // when
        지하철역_삭제(createdStationId);

        // then
        List<Station> stations = 지하철역_목록_조회();
        지하철역_개수_검증(stations, 0);
    }

    public static ValidatableResponse 지하철역_등록(String name) {
        StationRequest request = new StationRequest(name);

        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all();
    }

    private static void 응답_검증(ValidatableResponse response, HttpStatus status) {
        assertThat(response.extract().statusCode()).isEqualTo(status.value());
    }

    private static List<Station> 지하철역_목록_조회() {
        ValidatableResponse listResponse = RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all();
        return getJsonPathForResponse(listResponse).getList("$", Station.class);
    }

    private void 지하철역_등록_검증(List<Station> stations, String name) {
        assertThat(stations).containsAnyOf(new Station(name));
    }

    private void 지하철역_개수_검증(List<Station> stations, int size) {
        assertThat(stations).hasSize(size);
    }

    private long 응답_속성_조회(ValidatableResponse response, String property) {
        return getJsonPathForResponse(response)
                .getLong(property);
    }

    private void 지하철역_삭제(long stationId) {
        RestAssured.given().log().all()
                .pathParam("id", stationId)
                .when().delete("/stations/{id}")
                .then().log().all();
    }

    private static JsonPath getJsonPathForResponse(ValidatableResponse response) {
        return response.extract().jsonPath();
    }
}
