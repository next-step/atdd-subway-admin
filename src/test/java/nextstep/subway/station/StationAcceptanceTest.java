package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // given
        지하철역을_생성한다("강남역");

        // then
        List<String> allStations = 모든_지하철역을_조회한다("name");

        // then
        지하철역_이름이_조회된다(allStations, "강남역");
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
        지하철역을_생성한다("강남역");

        // when
        ExtractableResponse<Response> result = 지하철역을_생성한다("강남역");

        // then
        상태코드를_체크한다(result.statusCode(), HttpStatus.BAD_REQUEST.value());
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
        지하철역을_생성한다("역삼역");
        지하철역을_생성한다("강남역");

        // when
        List<String> allStations = 모든_지하철역을_조회한다("name");

        // then
        지하철역_이름이_조회된다(allStations, "강남역");
        지하철역_이름이_조회된다(allStations, "역삼역");
        조회한_지하철역의_사이즈를_조회한다(allStations, 2);
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
        long stationId = 지하철역_생성후_ID_를_리턴한다("역삼역");

        // when
        ExtractableResponse<Response> response = 지하철역을_삭제한다(stationId);

        // then
        상태코드를_체크한다(response.statusCode(), HttpStatus.NO_CONTENT.value());
    }

    public ExtractableResponse<Response> 지하철역을_생성한다(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    public long 지하철역_생성후_ID_를_리턴한다(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();

        Object getId = response.jsonPath().get("id");

        return Long.parseLong(String.valueOf(getId));
    }
    
    public List<String> 모든_지하철역을_조회한다(String target) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/stations")
                .then().log().all()
                .extract().jsonPath().getList(target, String.class);
    }

    public ExtractableResponse<Response> 지하철역을_삭제한다(Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/stations/" + id)
                .then().log().all()
                .extract();
    }

    private void 지하철역_이름이_조회된다(List<String> allStations, String value) {
        assertThat(allStations).containsAnyOf(value);
    }

    private void 상태코드를_체크한다(int statusCode, int expect) {
        assertThat(statusCode).isEqualTo(expect);
    }

    private void 조회한_지하철역의_사이즈를_조회한다(List<String> stations, int expect) {
        assertThat(stations).hasSize(expect);
    }
}
