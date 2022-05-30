package nextstep.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

@Sql("classpath:testdb/truncate.sql")
@DisplayName("지하철노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineAcceptanceTest extends AcceptanceTest {
    private final static String API_URL_LINES = "/lines";
    private final static String COLOR_RED = "bg-red-400";
    private final static String LINE_NUMBER_1 = "1호선";
    private final static String LINE_NUMBER_2 = "2호선";
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
    @DisplayName("지하철노선 생성")
    @Test
    void createLine() {
        // when
        registerLine(LINE_NUMBER_2);

        // then
        List<String> lineNames = findLines().jsonPath().getList("name", String.class);
        assertThat(lineNames).contains(LINE_NUMBER_2);

    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록 조회")
    @Test
    void getLines() {
        // given
        registerLine(LINE_NUMBER_1);
        registerLine(LINE_NUMBER_2);

        // when
        List<String> lineNames = findLines().jsonPath().getList("name", String.class);

        // then
        assertThat(lineNames).contains(LINE_NUMBER_1, LINE_NUMBER_2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선 조회")
    @Test
    void getLine() {
        // given
        String lineId = registerLine(LINE_NUMBER_2).jsonPath().getString("id");

        // when
        ExtractableResponse<Response> response = findLine(lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */

    @DisplayName("지하철노선 수정")
    @Test
    void updateLine() {
        // given
        String lineId = registerLine(LINE_NUMBER_2).jsonPath().getString("id");

        // when
        modifyLine(lineId, COLOR_RED);

        // then
        String actual = findLine(lineId).jsonPath().getString("color");
        assertThat(actual).isEqualTo(COLOR_RED);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선 삭제")
    @Test
    void deleteLine() {
        // given
        String lineId = registerLine(LINE_NUMBER_2).jsonPath().getString("id");

        // when
        removeLine(lineId);
        List<String> lineNames = findLines().jsonPath().getList("name", String.class);

        // then
        assertThat(lineNames.isEmpty() || !lineNames.contains(LINE_NUMBER_2)).isTrue();
    }

    private ExtractableResponse<Response> registerLine(String lineName) {
        Map<String, Object> lineMap = new HashMap<>();
        lineMap.put("name", lineName);
        lineMap.put("color", "red");
        lineMap.put("distance", 2);
        return sendPost(lineMap, API_URL_LINES);
    }

    private ExtractableResponse<Response> findLines() {
        return sendGet(API_URL_LINES);
    }

    private ExtractableResponse<Response> findLine(String lineId) {
        return sendGet(API_URL_LINES + "/{id}", lineId);
    }

    private ExtractableResponse<Response> modifyLine(String lineId, String color) {
        Map<String, Object> lineMap = new HashMap<>();
        lineMap.put("name", "redred");
        lineMap.put("color", color);
        return sendPut(lineMap, API_URL_LINES + "/{id}", lineId);
    }

    private ExtractableResponse<Response> removeLine(String lineId) {
        return sendDelete(API_URL_LINES + "/{id}", lineId);
    }
}
