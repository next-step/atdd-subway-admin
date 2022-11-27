package nextstep.subway.Line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.application.StationService;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.StationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    @Autowired
    StationService stationService;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleaner.execute();
        creatStation("강남역");
        creatStation("서초역");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = createLine(generateRequest("2호선", "green"));
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        List<String> lineNames = showLines();
        // then
        assertThat(lineNames).containsAnyOf("2호선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록 조회.")
    @Test
    void showCreatedLines() {
        // given
        LineRequest request1 = generateRequest("2호선", "green");
        LineRequest request2 = generateRequest("3호선", "orange");

        ExtractableResponse<Response> response1 = createLine(request1);
        ExtractableResponse<Response> response2 = createLine(request2);


        assertThat(response1.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        // when
        List<String> lineNames = showLines();
        // then
        assertThat(lineNames).hasSize(2);

    }
    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 특정 노선 조회.")
    @Test
    void retrieveTheLine() {
        // given
        ExtractableResponse<Response> response = createLine(generateRequest("2호선", "green"));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        // when
        ExtractableResponse<Response> retrieveResponse = retrieveLine(response.body().jsonPath().getLong("id"));
        // then
        assertThat(retrieveResponse.body().jsonPath().getString("name")).contains("2호선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 특정 노선 수정.")
    @Test
    void modifyTheLine() {
        // given
        ExtractableResponse<Response> response = createLine(generateRequest("2호선", "green"));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        // when
        LineRequest modifyRequest = generateRequest("3호선", "orange");
        ExtractableResponse<Response> modifyResponse = modifyLine(response, modifyRequest);
        assertThat(modifyResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        // then
        ExtractableResponse<Response> retrieveResponse = retrieveLine(response.body().jsonPath().getLong("id"));
        assertThat(retrieveResponse.body().jsonPath().getString("name")).contains("3호선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 특정 노선 삭제.")
    @Test
    void deleteTheLine() {
        // given
        LineRequest request = generateRequest("2호선", "green");
        ExtractableResponse<Response> response = createLine(request);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        // when
        ExtractableResponse<Response> modifyResponse = deleteLine(request, response);
        assertThat(modifyResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        // then
        ExtractableResponse<Response> retrieveResponse = retrieveLine(response.body().jsonPath().getLong("id"));
        assertThat(retrieveResponse.body().jsonPath().getString("name")).isNull();
    }

    @Transactional
    public void creatStation(String name) {
        stationService.saveStation(new StationRequest(name));
    }

    private List<String> showLines() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
    }

    private ExtractableResponse<Response> createLine(LineRequest params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> retrieveLine(long id) {
        return RestAssured.given().log().all()
                .when().get("/lines" + DELIMITER + id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> modifyLine(ExtractableResponse<Response> response, LineRequest modifyParams) {
        return RestAssured.given().log().all()
                .body(modifyParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines" + DELIMITER + response.body().jsonPath().getLong("id"))
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> deleteLine(LineRequest params, ExtractableResponse<Response> response) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines" + DELIMITER + response.body().jsonPath().getLong("id"))
                .then().log().all()
                .extract();
    }

    private LineRequest generateRequest(String name, String color) {
        return new LineRequest(name, color, 1, 2, 1);
    }
}
