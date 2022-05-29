package nextstep.subway.acceptance;

import static nextstep.subway.test.RequestUtils.requestCreate;
import static nextstep.subway.test.RequestUtils.requestGetAll;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.test.DatabaseClean;
import nextstep.subway.test.ExtractUtils;
import nextstep.subway.test.RequestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@DisplayName("구간 관련 기능 인수테스트 추가")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SectionAcceptanceTest {

    private static final String path = "lines/{id}/sections";
    private static final Map<String, Object> 이호선 = new HashMap<>();
    private static final Map<String, Long> STATION_IDS = new HashMap<>();

    @Autowired
    private DatabaseClean databaseClean;

    @LocalServerPort
    private int port;

    @BeforeAll
    static void init() {
        이호선.put("name", "2호선");
        이호선.put("color", "bg-green-200");
        이호선.put("distance", 10);
    }

    @BeforeEach
    private void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = this.port;
        }
        databaseClean.truncateAll();
        //given

        STATION_IDS.put("당산역", ExtractUtils.extractId(StationAcceptanceTest.createStation("당산역")));
        STATION_IDS.put("신도림역", ExtractUtils.extractId(StationAcceptanceTest.createStation("신도림역")));
        STATION_IDS.put("신대방역", ExtractUtils.extractId(StationAcceptanceTest.createStation("신대방역")));
        STATION_IDS.put("봉천역", ExtractUtils.extractId(StationAcceptanceTest.createStation("봉천역")));
        STATION_IDS.put("사당역", ExtractUtils.extractId(StationAcceptanceTest.createStation("사당역")));
        이호선.put("upStationId", STATION_IDS.get("신도림역"));
        이호선.put("downStationId", STATION_IDS.get("봉천역"));

        ExtractableResponse<Response> response = LineAcceptanceTest.creatLine(이호선);
        이호선.put("id", ExtractUtils.extractId(response));
    }

    /**
     * When 상행역과 하행역 사이에 새로운 구간을 등록하면
     * Then 상행역과 하행역 사이에 추가 역이 생성 된다.
     * When 노선 목록 조회 시
     * Then 연결된 구간의 지하철역들을 확인할 수 있다.
     */
    @DisplayName("구간 사이에 새로운 역을 등록한다")
    @Test
    void addSection() {

        //when
        ExtractableResponse<Response> createResponse = createSection((Long) 이호선.get("id"),
                STATION_IDS.get("신도림역"),
                STATION_IDS.get("신대방역"),
                3);
        //then
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        //when
        ExtractableResponse<Response> response = requestGetAll(LineAcceptanceTest.LINE_PATH);

        //then
        assertThat(ExtractUtils.extractNames(response)).containsExactly("신도림역", "신대방역", "봉천역");
        assertThat(ExtractUtils.extract("stations.distance", response, Integer.class)).contains(0, 3, 7);

    }

   /**
     * When 새로운 역을 상행 종점으로 등록하면
     * Then 새로운 역이 상행 좀점으로 추가 생성 된다.
     * When 노선 목록 조회 시
     * Then 연결된 구간의 지하철역들을 확인할 수 있다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addSection_up_station() {

        //when
        ExtractableResponse<Response> createResponse = createSection((Long) 이호선.get("id"),
                STATION_IDS.get("당산역"),
                STATION_IDS.get("신도림역"),
                10);
        //then
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        //when
        ExtractableResponse<Response> response = requestGetAll(LineAcceptanceTest.LINE_PATH);

        //then
        assertThat(ExtractUtils.extractNames(response)).containsExactly("당산역", "신도림역", "봉천역");
        assertThat(ExtractUtils.extract("stations.distance", response, Integer.class)).contains(0, 10, 10);

    }

   /**
     * When 새로운 역을 하행 종점으로 등록하면
     * Then 새로운 역이 하행 좀점으로 추가 생성 된다.
     * When 노선 목록 조회 시
     * Then 연결된 구간의 지하철역들을 확인할 수 있다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addSection_down_station() {

        //when
        ExtractableResponse<Response> createResponse = createSection((Long) 이호선.get("id"),
                STATION_IDS.get("봉천역"),
                STATION_IDS.get("사당역"),
                15);
        //then
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        //when
        ExtractableResponse<Response> response = requestGetAll(LineAcceptanceTest.LINE_PATH);

        //then
        assertThat(ExtractUtils.extractNames(response)).containsExactly("신도림역", "봉천역", "사당역");
        assertThat(ExtractUtils.extract("stations.distance", response, Integer.class)).contains(0, 10, 15);

    }

    private ExtractableResponse<Response> createSection(Long lineId,
                                                        long upStationId,
                                                        long downStationId,
                                                        long distance) {
        Map<String, Object> sectionParams = new HashMap<>();
        sectionParams.put("upStationId", upStationId);
        sectionParams.put("downStationId", downStationId);
        sectionParams.put("distance", distance);
        return RequestUtils.requestCreate(lineId, sectionParams, path);
    }

}
