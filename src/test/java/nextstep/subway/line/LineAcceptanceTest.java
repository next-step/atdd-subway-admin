package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        createLine("1호선", "dark-blue", "인천역", "소요산역", 100);

        // then
        assertThat(selectAllLine().body().jsonPath().getList("name")).contains("1호선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 2개의 노선을 조회할 수 있다
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getAllLine() {
        // given
        createLine("1호선", "dark-blue", "인천역", "소요산역", 100);
        createLine("2호선", "green", "신도림역", "성수역", 50);

        // when
        ExtractableResponse<Response> response = selectAllLine();

        // then
        assertThat(response.body().jsonPath().getList("id")).hasSize(2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse = createLine("1호선", "dark-blue", "인천역", "소요산역", 100);
        Long lineId = createResponse.body().jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> selectResponse = selectLineById(lineId);

        // then
        assertThat(selectResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(selectResponse.body().jsonPath().getLong("id")).isEqualTo(lineId);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void modifyLine() {
        // given
        ExtractableResponse<Response> createResponse = createLine("1호선", "dark-blue", "인천역", "소요산역", 100);
        Long lineId = createResponse.body().jsonPath().getLong("id");
        String name = createResponse.body().jsonPath().getString("name");

        // when
        LineUpdateRequest lineUpdateRequest = new LineUpdateRequest("1호선아님", "red");
        ExtractableResponse<Response> updateResponse = updateLine(lineId, lineUpdateRequest);
        ExtractableResponse<Response> selectResponse = selectLineById(lineId);

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(selectResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(selectResponse.body().jsonPath().getString("name")).isNotEqualTo(name);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = createLine("1호선", "dark-blue", "인천역", "소요산역", 100);
        Long lineId = createResponse.body().jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> deleteResponse = deleteLine(lineId);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> createLine(String name, String color, String upStationName, String downStationName, int distance) {
        Long upStationId = createStation(upStationName);
        Long downStationId = createStation(downStationName);
        LineRequest lineRequest = new LineRequest(name, color, upStationId, downStationId, distance);

        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private Long createStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract()
                .body().jsonPath().getLong("id");
    }

    private ExtractableResponse<Response> selectAllLine() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> selectLineById(Long lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/" + lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> updateLine(Long lineId, LineUpdateRequest lineUpdateRequest) {
        return RestAssured.given().log().all()
                .body(lineUpdateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/" + lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> deleteLine(Long lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/" + lineId)
                .then().log().all()
                .extract();
    }
}
