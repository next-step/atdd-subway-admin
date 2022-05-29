package nextstep.subway.acceptance;

import static nextstep.subway.test.RequestUtils.requestCreateBundle;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.test.DatabaseClean;
import nextstep.subway.test.ExtractUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;

@DisplayName("구간 관련 기능 인수테스트 추가")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SectionAcceptanceTest {

    private static final Map<String, Object> 이호선 = new HashMap<>();
    private static final Map<String, Object> STATION_IDS = new HashMap<>();

    @Autowired
    private DatabaseClean databaseClean;

    @LocalServerPort
    private int port;

    @BeforeAll
    static void init(){
        이호선.put("name", "2호선");
        이호선.put("color", "bg-green-200");
        이호선.put("distance", 10);
    }

    @BeforeEach
    private void setUp(){
        if(RestAssured.port == RestAssured.UNDEFINED_PORT){
            RestAssured.port = this.port;
        }
        databaseClean.truncateAll();
        //given
        ExtractableResponse<Response> response = LineAcceptanceTest.createLine(이호선, "신도림역", "봉천역");
        이호선.put("id", ExtractUtils.extractId(response));

        STATION_IDS.put("당산역",ExtractUtils.extractId(StationAcceptanceTest.createStation("당산역")));
        STATION_IDS.put("신대방역",ExtractUtils.extractId(StationAcceptanceTest.createStation("신대방역")));
        STATION_IDS.put("사당역",ExtractUtils.extractId(StationAcceptanceTest.createStation("사당역")));
    }

}
