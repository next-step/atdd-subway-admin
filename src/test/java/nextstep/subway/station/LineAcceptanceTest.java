package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineAcceptanceTest {

    private static final int SHIN_BUN_DANG_LINE = 0;
    private static final int SECOND_LINE = 1;
    private static final List<Map<String, Object>> LINE_PARAMS_BUNDLES;

    static {
        Map<String, Object> params1 = new HashMap<>();
        Map<String, Object> params2 = new HashMap<>();
        params1.put("name", "신분당선");
        params1.put("color", "bg-red-6000");
        params1.put("upStationId", 1);
        params1.put("downStationId", 2);
        params1.put("distance", 10);

        params2.put("name", "2호선");
        params2.put("color", "bg-green-6000");
        params2.put("upStationId", 1);
        params2.put("downStationId", 2);
        params2.put("distance", 10);
        LINE_PARAMS_BUNDLES = Arrays.asList(params1, params2);
    }

    @LocalServerPort
    int port;

    @BeforeEach
     void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        // 지하철역 2개 생성
        StationAcceptanceTest.requestCreateStations(StationAcceptanceTest.STATION_PARAMS_BUNDLES);
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 생성된 지하철 노선 정보를 응답받는다.
     * When 지하철 노선 목록을 조회하면
     * Then 생성한 노선을 찾을 수 있다.
     * */
    @Test
    void createLines(){

        //when
        ExtractableResponse<Response> createResponse = requestCreateLine(LINE_PARAMS_BUNDLES.get(SHIN_BUN_DANG_LINE));

        //then
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(createResponse.jsonPath().getList("stations.name")).contains("서울역","강남역");
        
        //when
        ExtractableResponse<Response> getResponse = requestGetLines();
        
        //then
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getResponse.jsonPath().getList("stations.name")).contains("신분당선");
    }


    private ExtractableResponse<Response> requestCreateLine(Map<String, Object> lineParams) {
        return RestAssured.given().log().all()
                .body(lineParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> requestGetLines() {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

}
