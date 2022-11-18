package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.utils.DatabaseCleanup;
import org.apache.http.protocol.HTTP;
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
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleanup databaseCleanup;
    @Autowired
    StationRepository stations;

    private static final String BASE_URL = "/lines";

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();

        stations.save(new Station("신사역"));
        stations.save(new Station("광교중앙"));
        stations.save(new Station("정자"));

    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void 지하철_노선_생성_테스트() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", "1");
        params.put("downStationId", "2");
        params.put("distance", "10");

        ExtractableResponse<Response> response = createLine(params);

        //then
        List<String> lines = retrieveLineNames();

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(lines).contains(params.get("name"))
        );
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void 지하철_노선_목록_조회_테스트() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", "1");
        params.put("downStationId", "2");
        params.put("distance", "10");

        ExtractableResponse<Response> response1 = createLine(params);

        params.clear();
        params.put("name", "분당선");
        params.put("color", "bg-green-600");
        params.put("upStationId", "1");
        params.put("downStationId", "3");
        params.put("distance", "10");

        ExtractableResponse<Response> response2 = createLine(params);

        // when
        List<String> lines = retrieveLineNames();

        // then
        assertAll(
                () -> assertThat(lines).hasSize(2),
                () -> assertThat(lines).contains("신분당선", "분당선")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void 지하철_노선_조회_테스트() {

        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", "1");
        params.put("downStationId", "2");
        params.put("distance", "10");

        ExtractableResponse<Response> response = createLine(params);

        // when
        List<String> line = retrieveLineByName(params.get("name"));

        // then
        assertAll(
                () -> assertThat(line).isNotNull(),
                () -> assertThat(line.get(0)).isEqualTo(params.get("name"))
        );

    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void 지하철_노선_수정_테스트() {

        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", "1");
        params.put("downStationId", "2");
        params.put("distance", "10");

        ExtractableResponse<Response> response1 = createLine(params);

        // when
        params.put("name", "수인분당선");

        ExtractableResponse<Response> response2 = updateLine(response1.header("Location"), params);

        // then
        List<String> line = retrieveLineByName(params.get("name"));


        assertAll(
                () -> assertThat(response2.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(line.get(0)).isEqualTo("수인분당선")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void 지하철_노선_삭제_테스트() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", "1");
        params.put("downStationId", "2");
        params.put("distance", "10");

        ExtractableResponse<Response> response1 = createLine(params);

        // when
        ExtractableResponse<Response> response2 = deleteLineByIe(response1.header("Location"));

        // then
        List<String> line = retrieveLineByName(params.get("name"));

        assertAll(
                () -> assertThat(response2.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(line).isEmpty()
        );

    }

    public static ExtractableResponse<Response> createLine(Map<String, String> params) {

        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post(BASE_URL)
                        .then().log().all()
                        .extract();

        return response;
    }

    public static List<String> retrieveLineNames() {
        List<String> lineNames =
                RestAssured.given().log().all()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().get(BASE_URL)
                        .then().log().all()
                        .extract()
                        .jsonPath().getList("name", String.class);

        return lineNames;
    }

    public static List<String> retrieveLineByName(String lineName) {
        List<String> line =
                RestAssured.given().log().all()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .queryParam("name", lineName)
                        .when().get(BASE_URL)
                        .then().log().all()
                        .extract()
                        .jsonPath().getList("name", String.class);
        return line;
    }

    private static ExtractableResponse<Response> updateLine(String location, Map<String, String> params) {

        String id = location.substring(location.lastIndexOf("/"));

        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().put(BASE_URL + id)
                        .then().log().all()
                        .extract();

        return response;
    }

    private static ExtractableResponse<Response> deleteLineByIe(String location) {

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(location)
                .then().log().all()
                .extract();

        return response;
    }
}
