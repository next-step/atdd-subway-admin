package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@Sql("/truncate.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    public static final String ENDPOINT = "/lines";

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        StationAcceptanceTest.createStation("지하철역");
        StationAcceptanceTest.createStation("새로운지하철역");
        StationAcceptanceTest.createStation("또다른지하철역");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void 지하철_노선_생성_조회() {
        // when
        ExtractableResponse<Response> response = createLine("신분당선", "bg-red-600", 1L, 2L, 10L);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> lineNames = getLinesIn("name", String.class);
        assertThat(lineNames).containsAnyOf("신분당선");

        List<String> lineColors = getLinesIn("color", String.class);
        assertThat(lineColors).containsAnyOf("bg-red-600");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void 지하철_노선_목록_조회() {
        // given
        createLine("신분당선", "bg-red-600", 1L, 2L, 10L);
        createLine("분당선", "bg-green-600", 1L, 3L, 13L);

        // when
        List<String> lineNames = getLinesIn("name", String.class);

        // then
        assertThat(lineNames).containsExactly("신분당선", "분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다
     */
    @DisplayName("지하철 노선 하나의 정보를 조회한다.")
    @Test
    void 지하철_노선_조회() {
        // given
        Long lineId = createLine("신분당선", "bg-red-600", 1L, 2L, 10L)
                .jsonPath().getLong("id");

        // when
        LineResponse line = getLine(lineId).jsonPath()
                .getObject("", LineResponse.class);


        // then
        assertThat(line.getName()).isEqualTo("신분당선");
        assertThat(line.getColor()).isEqualTo("bg-red-600");
    }

    /**
     * When 존재하지 않는 지하철 노선을 조회하면
     * Then 404 에러가 전달된다
     */
    @DisplayName("존재하지 않는 지하철 노선의 정보를 조회하려 한다.")
    @Test
    void 존재하지_않는_지하철_노선_조회() {
        // when
        ExtractableResponse<Response> response = getLine(10L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("생성한 지하철 노선을 수정한다.")
    @Test
    void 지하철_노선_수정() {
        // given
        ExtractableResponse<Response> response = createLine("신분당선", "bg-red-600", 1L, 2L, 10L);
        Long lineId = response.body().jsonPath().getLong("id");

        // when
        Map<String, String> params = new HashMap<>();
        params.put("name", "다른분당선");
        params.put("color", "bg-red-600");

        editLine(lineId, params);

        // then
        LineResponse line = getLine(lineId).jsonPath()
                .getObject("", LineResponse.class);
        assertThat(line.getName()).isEqualTo("다른분당선");
    }

    /**
     * When 존재하지 않는 지하철 노선을 수정하려 하면
     * Then 404 에러가 전달된다
     */
    @DisplayName("존재하지 않는 지하철 노선을 수정하려 한다.")
    @Test
    void 존재하지_않는_지하철_노선_수정() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("name", "다른분당선");
        params.put("color", "bg-red-600");

        ExtractableResponse<Response> response = editLine(10L, params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("생성한 지하철 노선을 삭제한다.")
    @Test
    void 생성한_지하철_노선_삭제() {
        // given
        ExtractableResponse<Response> response = createLine("신분당선", "bg-red-600", 1L, 2L, 10L);
        Long lineId = response.body().jsonPath().getLong("id");

        // when
        deleteLine(lineId);

        // then
        List<String> lineNames = getLinesIn("name", String.class);
        assertThat(lineNames).doesNotContain("신분당선");
    }

    /**
     * When 존재하지 않는 지하철 노선을 삭제하려 하면
     * Then 404 에러가 전달된다
     */
    @DisplayName("존재하지 않는 노선을 삭제하려 한다.")
    @Test
    void 존재하지_않는_노선_삭제() {
        // when
        ExtractableResponse<Response> response = deleteLine(1L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> deleteLine(Long lineId) {
        return RestAssured.given().log().all()
                .when().delete(ENDPOINT + "/" + lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> editLine(Long lineId, Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(ENDPOINT + "/" + lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createLine(
            String name,
            String color,
            Long upStationId,
            Long downStationId,
            Long distance) {

        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(ENDPOINT)
                .then().log().all()
                .extract();
    }

    private <T> List<T> getLinesIn(String path, Class<T> genericType) {
        return RestAssured.given().log().all()
                .when().get(ENDPOINT)
                .then().log().all()
                .extract().jsonPath().getList(path, genericType);
    }

    private ExtractableResponse<Response> getLine(Long lineId) {
        return RestAssured.given().log().all()
                .when().get(ENDPOINT + "/" + lineId)
                .then().log().all()
                .extract();
    }
}
