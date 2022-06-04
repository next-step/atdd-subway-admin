package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationRequest;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@Sql("/truncate.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    public static final String ENDPOINT = "/lines";

    @LocalServerPort
    int port;

    StationResponse 지하철역;
    StationResponse 새로운지하철역;
    StationResponse 또다른지하철역;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        지하철역 = StationAcceptanceTest.createStation(new StationRequest("지하철역")).as(StationResponse.class);
        새로운지하철역 = StationAcceptanceTest.createStation(new StationRequest("새로운지하철역")).as(StationResponse.class);
        또다른지하철역 = StationAcceptanceTest.createStation(new StationRequest("또다른지하철역")).as(StationResponse.class);
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void 지하철_노선_생성_조회() {
        // when
        ExtractableResponse<Response> response = createLine(new LineRequest(
                "신분당선", "bg-red-600", 지하철역.getId(), 새로운지하철역.getId(), 10));
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
        createLine(new LineRequest("신분당선", "bg-red-600", 지하철역.getId(), 새로운지하철역.getId(), 10));
        createLine(new LineRequest("분당선", "bg-green-600", 지하철역.getId(), 또다른지하철역.getId(), 13));

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
        String location = createLine(new LineRequest(
                "신분당선", "bg-red-600", 지하철역.getId(), 새로운지하철역.getId(), 10))
                .header("Location");

        // when
        LineResponse line = getLine(location).jsonPath()
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
        ExtractableResponse<Response> response = getLine("/lines/10");

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
        String location = createLine(new LineRequest(
                "신분당선", "bg-red-600", 지하철역.getId(), 새로운지하철역.getId(), 10))
                .header("Location");

        // when
        editLine(location, new LineRequest("다른분당선", ""));

        // then
        LineResponse line = getLine(location).jsonPath()
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
        ExtractableResponse<Response> response = editLine(
                "/lines/10", new LineRequest("다른분당선", "bg-red-600"));

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
        String location = createLine(new LineRequest("신분당선", "bg-red-600", 지하철역.getId(), 새로운지하철역.getId(), 10))
                .header("Location");

        // when
        deleteLine(location);

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
        ExtractableResponse<Response> response = deleteLine("lines/10");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> deleteLine(String location) {
        return RestAssured.given().log().all()
                .when().delete(location)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> editLine(String location, LineRequest body) {
        return RestAssured.given().log().all()
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(location)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> createLine(LineRequest body) {
        return RestAssured.given().log().all()
                .body(body)
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

    private ExtractableResponse<Response> getLine(String location) {
        return RestAssured.given().log().all()
                .when().get(location)
                .then().log().all()
                .extract();
    }
}
