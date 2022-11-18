package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {
    @LocalServerPort
    int port;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleaner.execute();
    }

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @Test
    @DisplayName("지하철역을 생성한다.")
    void createStationTest() {
        // when
        String stationName = "강남역";
        int responseCode = createStation(stationName).statusCode();

        // then
        assertThat(responseCode).isEqualTo(HttpStatus.CREATED.value());

        // then
        assertThat(getStationNames()).containsAnyOf(stationName);
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @Test
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    void createStationWithDuplicateNameTest() {
        // given
        String stationName = "강남역";
        createStation(stationName);

        // when
        int responseCode = createStation(stationName).statusCode();

        // then
        assertThat(responseCode).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @Test
    @DisplayName("지하철역을 조회한다.")
    void getStationsTest() {
        //given
        String stationName1 = "강남역";
        String stationName2 = "석촌역";
        createStation(stationName1);
        createStation(stationName2);

        //when
        final List<String> stationNames = getStationNames();

        //then
        //size 테스트
        assertThat(stationNames.size()).isEqualTo(2);
        //이름 포함 테스트
        assertThat(stationNames.stream()
                .allMatch(stationName -> stationName.equals(stationName1) || stationName.equals(stationName2)))
                .isTrue();
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @Test
    @DisplayName("지하철역을 제거한다.")
    void deleteStationTest() {
        //given
        String stationName = "강남역";
        String id = createStationAndGetId(stationName);

        //when
        deleteStation(id);

        //then
        final ExtractableResponse<Response> listResponse =
                RestAssured.given().log().all()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().get("/stations")
                        .then().log().all()
                        .extract();
        final List<String> stationNames = getStationNames();
        //size 테스트
        assertThat(stationNames.size())
                .isEqualTo(0);
        //이름 포함 테스트
        assertThat(stationNames.stream().anyMatch(stationName::equals))
                .isFalse();
    }

    /**
     * 주어진 이름으로 지하철역을 생성한다.
     */
    ExtractableResponse<Response> createStation(String stationName) {
        final Map<String, String> params1 = new HashMap<>();
        params1.put("name", stationName);

        return RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all().extract();
    }

    /**
     * 주어진 이름으로 지하철역을 생성 후 id를 조회한다.
     */
    String createStationAndGetId(String stationName) {
        return createStation(stationName).jsonPath().get("id").toString();
    }

    /**
     * 지하철역 목록을 조회한다.
     */
    List<String> getStationNames() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/stations")
                .then().log().all()
                .extract()
                .jsonPath().getList("name", String.class);
    }

    /**
     * 주어진id로 지하철역을 삭제한다.
     */
    void deleteStation(String id) {
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/stations/" + id)
                .then().log().all();
    }

}
