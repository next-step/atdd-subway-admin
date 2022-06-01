package nextstep.subway.acceptance;

import static nextstep.subway.test.RequestUtils.requestDeleteById;
import static nextstep.subway.test.RequestUtils.requestGetById;
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

@DisplayName("구간 관련 기능 인수테스트")
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
        STATION_IDS.put("구로디지털단지역", ExtractUtils.extractId(StationAcceptanceTest.createStation("구로디지털단지역")));
        STATION_IDS.put("봉천역", ExtractUtils.extractId(StationAcceptanceTest.createStation("봉천역")));
        STATION_IDS.put("사당역", ExtractUtils.extractId(StationAcceptanceTest.createStation("사당역")));
        이호선.put("upStationId", STATION_IDS.get("신도림역"));
        이호선.put("downStationId", STATION_IDS.get("봉천역"));

        ExtractableResponse<Response> response = LineAcceptanceTest.creatLine(이호선);
        이호선.put("id", ExtractUtils.extractId(response));
    }

    /**
     * When 상행역과 하행역 사이에 새로운 구간을 등록하면(상행역 기준)
     * Then 새로운 구간이 생성 된다.
     * And  조회시 새로등록한 구간의 지하철역을 구간순서에 맞게 확인할 수 있다.
     * When 상행역과 하행역 사이에 새로운 구간을 등록하면(하행역 기준)
     * Then 새로운 구간이 생성 된다.
     * And  조회시 새로등록한 구간의 지하철역을 구간순서에 맞게 확인할 수 있다.  
     * When 새로운 구간을 상행 종점으로 등록하면
     * Then 새로운 구간이 생성 된다.
     * And  조회시 새로등록한 구간의 지하철역을 구간순서에 맞게 확인할 수 있다.  
     * When 새로운 구간을 하행 종점으로 등록하면
     * Then 새로운 구간이 생성 된다.
     * And  조회시 새로등록한 구간의 지하철역을 구간순서에 맞게 확인할 수 있다.
     * */
    @DisplayName("지하철 노선에 구간을 등록 한다.")
    @Test
    void addSection(){
        //when
        ExtractableResponse<Response> createResponse1 = 구간_생성_요청(이호선, "신도림역", "신대방역", 3);

        //then
        생성_되었는지_상태_검증(createResponse1);
        추가된_구간에_대한_지하철역이_순서대로_조회_되는지_검증(이호선, "신도림역", "신대방역", "봉천역");

        //when
        ExtractableResponse<Response> createResponse2 = 구간_생성_요청(이호선, "구로디지털단지역", "봉천역", 5);

        //then
        생성_되었는지_상태_검증(createResponse2);
        추가된_구간에_대한_지하철역이_순서대로_조회_되는지_검증(이호선, "신도림역", "신대방역", "구로디지털단지역", "봉천역");

        //when
        ExtractableResponse<Response> createResponse3 = 구간_생성_요청(이호선, "당산역", "신도림역", 10);

        //then
        생성_되었는지_상태_검증(createResponse3);
        추가된_구간에_대한_지하철역이_순서대로_조회_되는지_검증(이호선, "당산역", "신도림역", "신대방역", "구로디지털단지역", "봉천역");

        //when
        ExtractableResponse<Response> createResponse4 = 구간_생성_요청(이호선, "봉천역", "사당역", 10);

        //then
        생성_되었는지_상태_검증(createResponse4);
        추가된_구간에_대한_지하철역이_순서대로_조회_되는지_검증(이호선, "당산역", "신도림역", "신대방역", "구로디지털단지역", "봉천역", "사당역");

    }

    /**
     * When 등록 되어있지 않는 지하철역들로 구간을 요청하면
     * Then 등록에 실패한다.
     * When 이미 등록된 구간으로 요청하면
     * Then 등록에 실패한다.
     * When 이미 등록된 구간으로 요청하면
     * When 기존 역 구간 길이보다 등록할 길이가 큰 경우
     * Then 등록에 실패한다.
     * When 기존 역 구간 길이와 등록할 길이가 같은 경우
     * Then 등록에 실패한다.
     * */
    @DisplayName("지하철 노선에 구간을 등록 실패한다.")
    @Test
    void addSection_fail() {

        //when
        ExtractableResponse<Response> createResponse1 = 구간_생성_요청(이호선, "신대방역", "사당역", 15);

        //then
        생성에_실패_했는지_상태_검증(createResponse1);

        //when
        ExtractableResponse<Response> createResponse2 = 구간_생성_요청(이호선, "신도림역", "봉천역", 10);

        //then
        생성에_실패_했는지_상태_검증(createResponse2);

        //when
        ExtractableResponse<Response> createResponse3 = 구간_생성_요청(이호선, "신도림역", "신대방역", 30);

        //then
        생성에_실패_했는지_상태_검증(createResponse3);

        //when
        ExtractableResponse<Response> createResponse4 = 구간_생성_요청(이호선, "신대방역", "봉천역", 10);

        //then
        생성에_실패_했는지_상태_검증(createResponse4);

    }

    /**
     * Given 구간을 등록하고
     * When  해당 구간에 대한 지하철역 삭제 요청 하면 (최상단 출발역)
     * Then  해당 지하철역을 포함하는 구간이 삭제 된다.
     * When  해당 구간에 대한 지하철역 삭제 요청 하면 (종착역)
     * Then  해당 지하철역을 포함하는 구간이 삭제 된다.
     * When  해당 구간에 대한 지하철역 삭제 요청 하면 (사이역)
     * Then  해당 지하철역을 포함하는 구간이 삭제 된다
     * When  없는 구간에 대해 삭제 요청하면
     * Then  삭제에 실패한다.
     * When  하나의 구간만 있는 경우에 삭제요청하면
     * Then  삭제에 실패한다.
     * */
    @DisplayName("지하철 노선에 구간을 삭제한다.")
    @Test
    void deleteSection() {
        //given
        구간_생성_요청(이호선, "당산역", "신도림역", 5);
        구간_생성_요청(이호선, "신도림역", "신대방역", 3);
        구간_생성_요청(이호선, "봉천역", "사당역", 10);

        //when
        ExtractableResponse<Response> deleteResponse1 = 구간_삭제_요청((Long) 이호선.get("id"), STATION_IDS.get("당산역"));

        //then
        구간이_삭제_되었는지_검증(deleteResponse1, 이호선,"당산역");

        //when
        ExtractableResponse<Response> deleteResponse2 = 구간_삭제_요청((Long) 이호선.get("id"), STATION_IDS.get("사당역"));

        //then
        구간이_삭제_되었는지_검증(deleteResponse2, 이호선, "사당역");

        //when
        ExtractableResponse<Response> deleteResponse3 = 구간_삭제_요청((Long) 이호선.get("id"), STATION_IDS.get("신도림역"));

        //then
        구간이_삭제_되었는지_검증(deleteResponse3, 이호선, "신도림역");

        //when
        ExtractableResponse<Response> deleteResponse4 = 구간_삭제_요청((Long) 이호선.get("id"), STATION_IDS.get("신도림역"));

        //then
        삭제에_실패_했는지_상태_검증(deleteResponse4);

        //when
        ExtractableResponse<Response> deleteResponse5 = 구간_삭제_요청((Long) 이호선.get("id"), STATION_IDS.get("신대방역"));

        //then
        삭제에_실패_했는지_상태_검증(deleteResponse5);

    }

    private ExtractableResponse<Response> 구간_생성_요청(
            Map<String, Object> line,
            String upStationName,
            String downStationName,
            long distance) {

        Map<String, Object> sectionParams = new HashMap<>();
        sectionParams.put("upStationId", STATION_IDS.get(upStationName));
        sectionParams.put("downStationId", STATION_IDS.get(downStationName));
        sectionParams.put("distance", distance);
        return RequestUtils.requestCreate((Long) line.get("id"), sectionParams, path);
    }

    private ExtractableResponse<Response> 구간_삭제_요청(long lineId, long stationId) {
        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("stationId", stationId);
        return requestDeleteById(path, queryParam, lineId);
    }

    private void 생성_되었는지_상태_검증(ExtractableResponse<Response> createResponse) {
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 추가된_구간에_대한_지하철역이_순서대로_조회_되는지_검증(Map<String, Object> line, String... orderStations) {
        ExtractableResponse<Response> response = requestGetById(LineAcceptanceTest.LINE_PATH, (Long) line.get("id"));
        List<String> extract = ExtractUtils.extract("stations.name", response, String.class);
        assertThat(extract).containsExactly(orderStations);
    }

    private void 생성에_실패_했는지_상태_검증(ExtractableResponse<Response> createResponse) {
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 구간이_삭제_되었는지_검증(ExtractableResponse<Response> deleteResponse,Map<String,Object> line,String stationName) {
        구간_삭제_되었는지_상태_검증(deleteResponse);
        ExtractableResponse<Response> getResponse = requestGetById(LineAcceptanceTest.LINE_PATH, (Long) line.get("id"));
        노선_조회시_해당_지하철역이_삭제_되었는지_검증(getResponse, stationName);
    }

    private void 구간_삭제_되었는지_상태_검증(ExtractableResponse<Response> deleteResponse) {
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 노선_조회시_해당_지하철역이_삭제_되었는지_검증(ExtractableResponse<Response> response, String stationName) {
        assertThat(ExtractUtils.extract("stations.name", response, String.class))
                .doesNotContain(stationName);
    }

    private void 삭제에_실패_했는지_상태_검증(ExtractableResponse<Response> deleteResponse) {
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
