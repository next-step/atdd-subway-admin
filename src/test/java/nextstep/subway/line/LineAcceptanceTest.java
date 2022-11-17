package nextstep.subway.line;

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
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    private Long upStationId;
    private Long downStationId;

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        upStationId = createStation("강남역").body().jsonPath().getLong("id");
        downStationId = createStation("판교역").body().jsonPath().getLong("id");
    }

    private ExtractableResponse<Response> createStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createLine(Map<String, Object> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> getLines() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void 지하철노선_생성후_조회() {
        // when
        Map<String, Object> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", 10);
        ExtractableResponse<Response> response = createLine(params);

        // then
        List<String> stationNames = getLines().jsonPath().getList("name", String.class);
        assertThat(stationNames).containsAnyOf("신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void 지하철노선_목록_조회() {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", 10);
        createLine(params);

        Map<String, Object> params2 = new HashMap<>();
        params2.put("name", "2호선");
        params2.put("color", "bg-green-600");
        params2.put("upStationId", upStationId);
        params2.put("downStationId", downStationId);
        params2.put("distance", 10);
        createLine(params2);

        // when
        List<Long> ids = getLines().body().jsonPath().getList("id", Long.class);

        // then
        assertThat(ids).hasSize(2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선을 조회한다.")
    @Test
    void 지하철노선_조회() {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", 10);
        Long createdLineId = createLine(params).body().jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> extract = RestAssured.given().log().all()
                .when()
                .get("/lines/{id}", createdLineId)
                .then().log().all().extract();

        // then
        assertThat(extract.body().jsonPath().getString("name")).isEqualTo("신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선을 수정한다.")
    @Test
    void 지하철노선_수정() {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", 10);
        Long createdLineId = createLine(params).body().jsonPath().getLong("id");

        Map<String, String> reqBody = new HashMap<>();
        reqBody.put("name", "다른분당선");
        reqBody.put("color", "bg-green-600");
        // when
        ExtractableResponse<Response> updateResponse = RestAssured.given().log().all()
                .body(reqBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/{id}", createdLineId)
                .then().log().all()
                .extract();


        // then
        ExtractableResponse<Response> findResponse = RestAssured.given().log().all()
                .when()
                .get("/lines/{id}", createdLineId)
                .then().log().all().extract();
        assertAll(() -> assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(findResponse.body().jsonPath().getString("name")).isEqualTo("다른분당선"));
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선을 삭제한다.")
    @Test
    void 지하철노선_삭제() {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", 10);
        Long createdLineId = createLine(params).body().jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> deleteResponse = RestAssured.given().log().all()
                .when()
                .delete("/lines/{id}", createdLineId)
                .then().log().all()
                .extract();

        // then
        ExtractableResponse<Response> findResponse = getLines();
        assertAll(() -> assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(getLines().body().jsonPath().getList("id")).isEmpty());
    }
}
