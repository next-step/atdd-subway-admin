package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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
    @DisplayName("지하철 노선을 생성하여 조회 시 있는지 확인한다.")
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

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회시 2개의 노선을 조회할 수 있다.
     * */
    @DisplayName("생성된 지하철 노선들이 목록 조회 시 있는지 확인한다.")
    @Test
    void showLines() {

        //given
        requestCreateLines(LINE_PARAMS_BUNDLES);

        //when
        ExtractableResponse<Response> response = requestGetLines();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("name")).contains("신분당선","2호선");

    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     * */
    @DisplayName("지하철 노선을 개별 조회한다.")
    @Test
    void showLine(){
        //given
        requestCreateLine(LINE_PARAMS_BUNDLES.get(SHIN_BUN_DANG_LINE));

        //when
        ExtractableResponse<Response> response = requestGetLine(1L);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("name")).contains("신분당선");
    }

   /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     * */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine(){
        //given
        requestCreateLine(LINE_PARAMS_BUNDLES.get(SHIN_BUN_DANG_LINE));

        //when
        ExtractableResponse<Response> response = requestDeleteLine(1L);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(requestGetLine(1L).statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     * */
    @DisplayName("지하철 노선을 업데이트한다.")
    @Test
    void updateLine(){
        //given
        requestCreateLine(LINE_PARAMS_BUNDLES.get(SHIN_BUN_DANG_LINE));

        //when
        Map<String, Object> lineParams = LINE_PARAMS_BUNDLES.get(SHIN_BUN_DANG_LINE);
        lineParams.put("name" , "분당선");
        lineParams.put("color" , "bg-yellow-600");
        ExtractableResponse<Response> response = requestUpdateLine(1L,lineParams);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(requestGetLine(1L).jsonPath().getString("name")).contains("분당선");
        assertThat(requestGetLine(1L).jsonPath().getString("color")).contains("bg-yellow-600");
    }


    private List<ExtractableResponse<Response>> requestCreateLines(List<Map<String, Object>> stationsParamsBundle) {
        List<ExtractableResponse<Response>> responses = new ArrayList<>();
        for (Map<String, Object> stationParams : stationsParamsBundle) {
            responses.add(requestCreateLine(stationParams));
        }
        return responses;
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

    private ExtractableResponse<Response> requestGetLine(long lineId) {
        return RestAssured.given().log().all()
                .accept(ContentType.JSON)
                .when().delete("/lines/{id}", lineId)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> requestDeleteLine(long lineId) {
        return RestAssured.given().log().all()
                .accept(ContentType.JSON)
                .when().delete("/stations/{id}", lineId)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> requestUpdateLine(long lineId, Map<String,Object> lineParams) {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineParams)
                .when().put("/stations/{id}", lineId)
                .then().log().all()
                .extract();
    }

}
