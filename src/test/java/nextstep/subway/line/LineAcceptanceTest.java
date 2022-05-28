package nextstep.subway.line;

import static nextstep.subway.station.StationAcceptanceTest.지하철역_생성;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import java.util.Arrays;
import java.util.List;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

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

    /*
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Long downStationId = 지하철역_생성("신사역").extract().jsonPath().getLong("id");
        Long upStationId = 지하철역_생성("광교역").extract().jsonPath().getLong("id");

        // when
        노선_생성("신분당선", "bg-red-600", downStationId, upStationId, 10);

        // then
        List<String> lineList = 노션_목록_조회().jsonPath().getList("name", String.class);
        assertThat(lineList).containsAnyOf("신분당선");
    }

    /*
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        Long downStationId = 지하철역_생성("신사역").extract().jsonPath().getLong("id");
        Long upStationId = 지하철역_생성("광교역").extract().jsonPath().getLong("id");
        노선_생성("신분당선", "bg-red-600", downStationId, upStationId, 10);

        downStationId = 지하철역_생성("신림역").extract().jsonPath().getLong("id");
        upStationId = 지하철역_생성("봉천역").extract().jsonPath().getLong("id");
        노선_생성("2호선", "bg-blue-600", downStationId, upStationId, 10);

        // when
        List<Object> stationList = 노션_목록_조회().jsonPath().getList("$");

        // then
        assertThat(stationList).hasSize(2);
    }

    /*
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("노선 하나를 조회한다.")
    @Test
    void getLine() {
        // given
        StationResponse downStation = 지하철역_생성("신사역").extract().as(StationResponse.class);
        StationResponse upStation = 지하철역_생성("광교역").extract().as(StationResponse.class);
        Long createLineId = 노선_생성("신분당선", "bg-red-600", downStation.getId(), upStation.getId(), 10)
            .extract().jsonPath().getLong("id");

        // when
        LineResponse lineResponse = 노션_조회(createLineId).as(LineResponse.class);

        // then
        LineResponse expectedResponse =
            new LineResponse(createLineId, "신분당선", "bg-red-600", Arrays.asList(downStation, upStation));
        assertThat(lineResponse).isEqualTo(expectedResponse);
    }

    /*
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("노선을 수정한다.")
    @Test
    void putLine() {
        // given
        StationResponse downStation = 지하철역_생성("신사역").extract().as(StationResponse.class);
        StationResponse upStation = 지하철역_생성("광교역").extract().as(StationResponse.class);
        Long createLineId = 노선_생성("신분당선", "bg-red-600", downStation.getId(), upStation.getId(), 10)
            .extract().jsonPath().getLong("id");

        // when
        LineRequest lineRequest = new LineRequest("구분당선", "bg-blue-600", downStation.getId(), upStation.getId(), 10);
        ExtractableResponse<Response> lineResponse = 노션_수정(createLineId, lineRequest);

        // then
        assertThat(lineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /*
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        StationResponse downStation = 지하철역_생성("신사역").extract().as(StationResponse.class);
        StationResponse upStation = 지하철역_생성("광교역").extract().as(StationResponse.class);
        Long createLineId = 노선_생성("신분당선", "bg-red-600", downStation.getId(), upStation.getId(), 10)
            .extract().jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> lineResponse = 노션_삭제(createLineId);

        // then
        assertThat(lineResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 노션_삭제(Long lindId) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete("/lines/{lineId}", lindId)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 노션_수정(Long lindId, LineRequest lineRequest) {
        return RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put("/lines/{lineId}", lindId)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 노션_조회(Long lineId) {
        return RestAssured.given().log().all()
            .when().get("/lines/{lineId}", lineId)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 노션_목록_조회() {
        return RestAssured.given().log().all()
            .when().get("/lines")
            .then().log().all()
            .extract();
    }

    private ValidatableResponse 노선_생성(String name, String color, Long upStationId, Long downStationId, int distance) {
        LineRequest lineRequest = new LineRequest(name, color, upStationId, downStationId, distance);

        return RestAssured
            .given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all();
    }

}
