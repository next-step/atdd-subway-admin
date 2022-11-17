package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Line;
import nextstep.subway.utils.DatabaseCleanup;
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

    private static final String BASE_URL = "/lines";

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();
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
        List<Line> line = retrieveLineByName(params.get("name"));

        // then
        assertAll(
                () -> assertThat(line).isNotEmpty(),
                () -> assertThat(line.get(0).getName()).isEqualTo(params.get("name")),
                () -> assertThat(line.get(0).getColor()).isEqualTo(params.get("color"))
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

    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void 지하철_노선_삭제_테스트() {

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

    public static List<Line> retrieveLineByName(String lineName) {
        List<Line> line =
                RestAssured.given().log().all()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .queryParam("name", lineName)
                        .when().get(BASE_URL)
                        .then().log().all()
                        .extract()
                        .jsonPath().getList(".", Line.class);

        return line;
    }

}
