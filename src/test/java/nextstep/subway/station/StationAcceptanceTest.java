package nextstep.subway.station;

import static nextstep.subway.utils.AssertionsUtils.assertBadRequest;
import static nextstep.subway.utils.AssertionsUtils.assertCreated;
import static nextstep.subway.utils.AssertionsUtils.assertNoContent;
import static nextstep.subway.utils.AssertionsUtils.assertOk;
import static nextstep.subway.utils.ResponseBodyExtractUtils.getId;
import static nextstep.subway.utils.RestAssuredUtils.delete;
import static nextstep.subway.utils.RestAssuredUtils.get;
import static nextstep.subway.utils.RestAssuredUtils.post;
import static nextstep.subway.utils.StationsAcceptanceUtils.generateStation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import nextstep.subway.dto.station.StationRequest;
import nextstep.subway.utils.TearDownUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestConstructor(autowireMode = AutowireMode.ALL)
@Import(TearDownUtils.class)
public class StationAcceptanceTest {

    private final TearDownUtils tearDownUtils;

    public StationAcceptanceTest(TearDownUtils tearDownUtils) {
        this.tearDownUtils = tearDownUtils;
    }

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        tearDownUtils.tableClear();
    }

    public static final String STATION_BASE_URL = "/stations";

    /**
     * When 지하철역을 생성하면 Then 지하철역이 생성된다 Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // Given
        final String stationName = "강남역";
        final StationRequest stationRequest = new StationRequest(stationName);

        // When
        Response stationCreateResponse = post(STATION_BASE_URL, stationRequest).extract().response();

        // Then
        Response getAllStationsResponse = get(STATION_BASE_URL).extract().response();
        JsonPath jsonPath = getAllStationsResponse.jsonPath();
        assertAll(
            () -> assertCreated(stationCreateResponse),
            () -> assertOk(getAllStationsResponse),
            () -> assertThat(jsonPath.getList("name"))
                .as("지하철역 목록 조회 시 생성한 지하철역 이름 포함 여부 검증")
                .containsAnyOf(stationName)
        );
    }

    /**
     * Given 지하철역을 생성하고 When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면 Then 지하철역 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        final String stationName = "판교역";
        Response creationResponse = generateStation(stationName);

        // when
        Response duplicationCreateResponse = generateStation(stationName);

        // then
        assertAll(
            () -> assertCreated(creationResponse),
            () -> assertBadRequest(duplicationCreateResponse)
        );
    }

    /**
     * Given 2개의 지하철역을 생성하고 When 지하철역 목록을 조회하면 Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // Given
        final String firstStationName = "논현역";
        final String secondStationName = "신논현역";
        generateStation(firstStationName);
        generateStation(secondStationName);

        // When
        Response getAllStationsResponse = get(STATION_BASE_URL).extract().response();

        // Then
        JsonPath jsonPath = getAllStationsResponse.jsonPath();
        assertAll(
            () -> assertOk(getAllStationsResponse),
            () -> assertThat(jsonPath.getList("name"))
                .as("지하철역 목록 조회 응답에 생성한 두개의 지하철역 이름 포함 여부 검증")
                .containsAnyOf(firstStationName, secondStationName)
        );
    }

    /**
     * Given 지하철역을 생성하고 When 그 지하철역을 삭제하면 Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // Given
        final String stationName = "선릉역";
        final Response generateStationResponse = generateStation(stationName);
        final String stationId = getId(generateStationResponse);

        // When
        Response deleteResponse = delete(STATION_BASE_URL, stationId).extract().response();

        // Then
        Response getAllStationsResponse = get(STATION_BASE_URL).extract().response();
        JsonPath jsonPath = getAllStationsResponse.jsonPath();
        assertAll(
            () -> assertNoContent(deleteResponse),
            () -> assertOk(getAllStationsResponse),
            () -> assertThat(jsonPath.getList("name"))
                .as("지하철역 목록 조회 응답에 삭제한 지하철역 미포함 여부 검증")
                .doesNotContain(stationName)
        );
    }
}
