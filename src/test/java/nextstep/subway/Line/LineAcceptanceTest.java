package nextstep.subway.Line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.Db.DatabaseCleanup;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.station.StationAcceptanceTest;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleanup databaseCleanup;

    private final String LINE_URL = "/lines";

    @BeforeEach
    public void setUp() throws Exception {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseCleanup.afterPropertiesSet();
        }
        databaseCleanup.tableClear();
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 노선이 생성된다
     * Then 노선 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("노선을 생성한다.")
    @Test
    void createLineTest() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_신분당선();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        ExtractableResponse<Response> lineResponse = selectLine(getId(response));
        assertThat(toLineResponse(lineResponse).getName()).isEqualTo("신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다
     */
    @DisplayName("노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        지하철_노선_생성_신분당선();
        지하철_노선_생성_2호선();

        // when
        ExtractableResponse<Response> response = selectAllLine();
        List<LineResponse> lines = response.jsonPath().getList(".", LineResponse.class);

        // then
        assertAll(
                () -> assertThat(lines).hasSize(2),
                () -> assertThat(lines.stream().map(lineResponse -> lineResponse.getName()).collect(Collectors.toList())).contains("신분당선"),
                () -> assertThat(lines.stream().map(lineResponse -> lineResponse.getName()).collect(Collectors.toList())).contains("2호선")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다
     */
    @DisplayName("노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_신분당선();

        // when
        ExtractableResponse<Response> response = selectLine(getId(createResponse));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        LineResponse line = toLineResponse(response);
        assertAll(
                () -> assertThat(line.getName()).isEqualTo("신분당선"),
                () -> assertThat(line.getColor()).isEqualTo("bg-red-600")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_신분당선();

        // when
        Map<String, String> params = new HashMap<>();
        params.put("name", "new 신분당선");
        params.put("color", "bg-green-600");

        Long id = getId(createResponse);
        ExtractableResponse<Response> response = updateLine(id, params);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        LineResponse lineResponse = toLineResponse(selectLine(id));
        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo("new 신분당선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("bg-green-600")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_신분당선();

        // when
        Long id = getId(createResponse);
        ExtractableResponse<Response> response = deleteLine(id);
        ExtractableResponse<Response> afterDelete = selectLine(id);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(afterDelete.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        );
    }

    public ExtractableResponse<Response> 지하철_노선_생성_신분당선() {
        return createLine("신분당선", "bg-red-600", "광교역", "신사역", 10L);
    }

    public ExtractableResponse<Response> 지하철_노선_생성_2호선() {
        return createLine("2호선", "bg-green-600", "까치산", "신도림", 20L);
    }

    public ExtractableResponse<Response> createLine(String name, String color, String upStation, String downStation, Long distance) {
        Map<String, String> params = createParams(name, color, upStation, downStation, distance);
        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("/lines")
                        .then().log().all()
                        .extract();

        return response;
    }

    public Map<String, String> createParams(String name, String color, String upStation, String downStation, Long distance) {
        Long upStationId = StationAcceptanceTest.createStation(upStation)
                .as(StationResponse.class)
                .getId();

        Long downStationId = StationAcceptanceTest.createStation(downStation)
                .as(StationResponse.class)
                .getId();

        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        params.put("distance", String.valueOf(distance));

        return params;
    }

    public ExtractableResponse<Response> selectAllLine() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(LINE_URL)
                .then().log().all()
                .extract();
    }

    public ExtractableResponse<Response> selectLine(Long id) {
        return  RestAssured.given().log().all()
                .when().get(LINE_URL+ "/" + String.valueOf(id))
                .then().log().all()
                .extract();
    }

    public Long getId(ExtractableResponse<Response> response) {
        return response.as(LineResponse.class).getId();
    }

    public LineResponse toLineResponse(ExtractableResponse<Response> response) {
        return response.as(LineResponse.class);
    }

    public ExtractableResponse<Response> updateLine(Long id, Map<String, String> params) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().put(LINE_URL+ "/" + id)
                .then().log().all()
                .extract();
    }

    public ExtractableResponse<Response> deleteLine(Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(LINE_URL+ "/" + id)
                .then().log().all()
                .extract();
    }
}
