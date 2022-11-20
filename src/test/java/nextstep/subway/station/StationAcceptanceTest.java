package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.DatabaseCleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    @AfterEach
    public void cleanUp() {
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
        // given
        String stationName = "강남역";

        // when
        ExtractableResponse<Response> apiResponse = createStation(stationName);

        // then
        assertThat(apiResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(stationName.equals(apiResponse.jsonPath().getObject("name",String.class)))
                .isTrue();
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
        int resultCode = createStationAndGetResponseCode(stationName);

        //then
        assertThat(resultCode).isEqualTo(HttpStatus.BAD_REQUEST.value());
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
        final Set<String> results = new HashSet<>(getStationNames());

        //then
        assertThat(results).containsExactly(stationName1, stationName2);
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
    static ExtractableResponse<Response> createStation(String stationName) {
        final Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all().extract();
    }

    /**
     * 주어진 이름으로 지하철역을 생성 후 id를 조회한다.
     */
    public static String createStationAndGetId(String stationName) {
        return createStation(stationName).jsonPath().get("id").toString();
    }
    /**
     * 주어진 이름으로 지하철역을 생성 후 상태코드를 조회한다.
     */
    static int createStationAndGetResponseCode(String stationName) {
        return createStation(stationName).statusCode();
    }

    /**
     * 지하철역 목록을 조회한다.
     */
    public static List<String> getStationNames() {
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
