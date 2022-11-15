package nextstep.subway.lineStation;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.DatabaseCleaner;
import nextstep.subway.dto.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineStationAcceptanceTest {
    @LocalServerPort
    int port;
    @Autowired
    private DatabaseCleaner databaseCleaner;

    private JsonPath 강남역;
    private JsonPath 선릉역;
    private JsonPath 이호선;
    private final int DISTANCE = 10;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleaner.clear();

        // given
        강남역 = 지하철_역_생성("강남역");
        선릉역 = 지하철_역_생성("선릉역");
        이호선 = 지하철_노선_생성("2호선", "green", getId(강남역), getId(선릉역), DISTANCE);
    }

    /**
     * Given 역 2개와 노선을 생성하고
     * When 사이에 새로운 역을 등록하면
     * Then 노선 조회 시 3개 역이 조회된다.
     */
    @DisplayName("역 사이에 새로운 역을 등록한다.")
    @Test
    void 역_사이에_새로운_역_등록() {
        // when
        JsonPath 역삼역 = 지하철_역_생성("역삼역");
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("upStationId", getId(강남역));
        paramMap.put("downStationId", getId(역삼역));
        paramMap.put("distance", 5);
        ExtractableResponse<Response> postResponse = 지하철_구간_추가(getId(이호선), paramMap);

        // then
        ExtractableResponse<Response> getResponse = 노선_아이디로_지하철역_조회(getId(이호선));
        assertThat(postResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getResponse.jsonPath().getList("stations")).hasSize(3);
    }

    /**
     * Given 역 2개와 노선을 생성하고
     * When 새로운 역을 상행 종점으로 등록하면
     * Then 노선 조회 시 첫번째 역이 새로운 역과 일치한다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void 새로운_역을_상행_종점으로_등록() {
        // when
        JsonPath 교대역 = 지하철_역_생성("교대역");
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("upStationId", getId(교대역));
        paramMap.put("downStationId", getId(강남역));
        paramMap.put("distance", 5);
        ExtractableResponse<Response> postResponse = 지하철_구간_추가(getId(이호선), paramMap);

        // then
        ExtractableResponse<Response> getResponse = 노선_아이디로_지하철역_조회(getId(이호선));
        List<String> stationNames = getResponse.jsonPath().getList("stations.name", String.class);
        assertThat(postResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(stationNames).hasSize(3);
        assertThat(stationNames).containsExactly("교대역", "강남역", "선릉역");
    }

    /**
     * Given 역 2개와 노선을 생성하고
     * When 새로운 역을 하행 종점으로 등록하면
     * Then 노선 조회 시 마지막 역이 새로운 역과 일치한다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void 새로운_역을_하행_종점으로_등록() {
        // when
        JsonPath 삼성역 = 지하철_역_생성("삼성역");
        Map<String, Object> paramMap = 지하철_구간_추가_파라미터_생성(getId(선릉역), getId(삼성역), 5);
        ExtractableResponse<Response> postResponse = 지하철_구간_추가(getId(이호선), paramMap);

        // then
        ExtractableResponse<Response> getResponse = 노선_아이디로_지하철역_조회(getId(이호선));
        List<String> stationNames = getResponse.jsonPath().getList("stations.name", String.class);
        assertThat(postResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(stationNames).hasSize(3);
        assertThat(stationNames).containsExactly("강남역", "선릉역", "삼성역");
    }

    /**
     * Given 역 2개와 노선을 생성하고
     * When 사이에 기존 역 사이 길이보다 크거나 같은 역을 등록하면
     * Then 등록이 안된다.
     */
    @DisplayName("기존 역 사이 길이보다 크거나 같은 역을 등록한다.")
    @Test
    void 기존_역_사이와_같거나_긴_역_등록() {
        // when
        JsonPath 역삼역 = 지하철_역_생성("역삼역");
        Map<String, Object> paramMap = 지하철_구간_추가_파라미터_생성(getId(역삼역), getId(선릉역), DISTANCE);
        ExtractableResponse<Response> postResponse = 지하철_구간_추가(getId(이호선), paramMap);

        // then
        ExtractableResponse<Response> getResponse = 노선_아이디로_지하철역_조회(getId(이호선));
        assertThat(postResponse.statusCode()).isEqualTo(예외_발생());
        assertThat(getResponse.jsonPath().getList("stations")).hasSize(2);
    }

    /**
     * Given 역 2개와 노선을 생성하고
     * When 생성한 노선을 다시 노선에 등록하면
     * Then 등록이 안된다.
     */
    @DisplayName("이미 등록된 상행역/하행역을 등록한다.")
    @Test
    void 이미_등록된_상행역_하행역을_등록() {
        // when
        Map<String, Object> paramMap = 지하철_구간_추가_파라미터_생성(getId(강남역), getId(선릉역), 5);
        ExtractableResponse<Response> postResponse = 지하철_구간_추가(getId(이호선), paramMap);

        // then
        ExtractableResponse<Response> getResponse = 노선_아이디로_지하철역_조회(getId(이호선));
        assertThat(postResponse.statusCode()).isEqualTo(예외_발생());
        assertThat(getResponse.jsonPath().getList("stations")).hasSize(2);
    }

    /**
     * When 상행역 또는 하행역이 포함되지 않는 구간으로 등록하면
     * Then 등록이 안된다.
     */
    @DisplayName("상행역/하행역이 포함되지 않는 구간으로 등록한다.")
    @Test
    void 상행역_하행역이_포함되지_않는_구간으로_등록() {
        // when
        JsonPath 신도림역 = 지하철_역_생성("신도림역");
        JsonPath 대림역 = 지하철_역_생성("대림역");
        Map<String, Object> paramMap = 지하철_구간_추가_파라미터_생성(getId(신도림역), getId(대림역), 5);
        ExtractableResponse<Response> postResponse = 지하철_구간_추가(getId(이호선), paramMap);

        // then
        ExtractableResponse<Response> getResponse = 노선_아이디로_지하철역_조회(getId(이호선));
        assertThat(postResponse.statusCode()).isEqualTo(예외_발생());
        assertThat(getResponse.jsonPath().getList("stations")).hasSize(2);
    }

    /**
     * Given 역 3개와 노선을 생성하고
     * When 종점을 제거하면
     * Then 노선에 역 2개가 남는다.
     */
    @DisplayName("노선에서 종점을 제거한다.")
    @Test
    void 노선에서_종점_제거() {

    }

    /**
     * Given 역 3개와 노선을 생성하고
     * When 가운데 역을 제거하면
     * Then 노선에 역 2개가 남는다.
     */
    @DisplayName("노선에서 가운데 역을 제거한다.")
    @Test
    void 노선에서_가운데_역_제거() {

    }

    /**
     * Given 역 2개와 노선을 생성하고
     * When 역을 제거하면
     * Then 제거가 안된다.
     */
    @DisplayName("구간이 하나인 노선에서 역을 제거한다.")
    @Test
    void 구간이_하나인_노선에서_역_제거() {
    }

    /**
     * Given 역 2개와 노선을 생성하고
     * When 노선에 등록되지 않은 역을 제거하면
     * Then 예외가 발생한다.
     */
    @DisplayName("노선에 등록되지 않은 역을 제거한다.")
    @Test
    void 노선에_등록되지_않은_역_제거() {
        // when
        JsonPath 신도림역 = 지하철_역_생성("신도림역");
        ExtractableResponse<Response> deleteResponse = 지하철_구간_제거(getId(이호선), getId(신도림역));

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(예외_발생());
    }

    private JsonPath 지하철_역_생성(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract()
                .jsonPath();
    }

    private JsonPath 지하철_노선_생성(String name, String color, Long upStationId,
                                                     Long downStationId, int distance) {
        LineRequest lineRequest = new LineRequest(name, color, upStationId, downStationId, distance);

        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract()
                .jsonPath();
    }

    private Map<String, Object> 지하철_구간_추가_파라미터_생성(Long upStationId, Long downStationId, int distance) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("upStationId", upStationId);
        paramMap.put("downStationId", downStationId);
        paramMap.put("distance", distance);

        return paramMap;
    }

    private ExtractableResponse<Response> 지하철_구간_추가(Long lineId, Map<String, Object> paramMap) {
        return RestAssured.given().log().all()
                .body(paramMap)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_구간_제거(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/" + lineId + "/sections?stationId=" + stationId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 노선_아이디로_지하철역_조회(Long lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/" + lineId)
                .then().log().all()
                .extract();
    }

    private Long getId(JsonPath jsonPath) {
        return jsonPath.getLong("id");
    }
    
    private int 예외_발생() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
