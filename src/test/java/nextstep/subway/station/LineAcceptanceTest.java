package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import org.assertj.core.util.Maps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@DisplayName("지하철노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineAcceptanceTest extends AcceptanceTest {
    private final static String API_URL_LINES = "/lines";
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
        registerLine("2호선");

        // then
        List<String> lineNames = findLines().jsonPath().getList("name", String.class);
        assertThat(lineNames).contains("2호선");

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
        registerLine("1호선");
        registerLine("2호선");

        // when
        List<String> lineNames = findLines().jsonPath().getList("name", String.class);

        // then
        assertThat(lineNames).contains("1호선", "2호선");
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
        String lineId = registerLine("2호선").jsonPath().getString("id");

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
        final String newColor = "bg-red-400";

        // given
        String lineId = registerLine("2호선").jsonPath().getString("id");

        // when
        ExtractableResponse<Response> response = modifyLine(lineId, Maps.newHashMap("color", newColor));

        // then
        String actual = findLine(lineId).jsonPath().getString("color");
        assertThat(actual).isEqualTo(newColor);
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
        String lineId = registerLine("2호선").jsonPath().getString("id");

        // when
        removeLine(lineId);
        List<String> lineNames = findLines().jsonPath().getList("name", String.class);

        // then
        assertThat(lineNames.isEmpty() || !lineNames.contains("2호선")).isTrue();
    }

    private ExtractableResponse<Response> registerLine(String lineName) {
        return sendPost(Maps.newHashMap("name", lineName), API_URL_LINES);
    }

    private ExtractableResponse<Response> findLines() {
        return sendGet(API_URL_LINES);
    }

    private ExtractableResponse<Response> findLine(String lineId) {
        return sendGet(API_URL_LINES + "/{id}", lineId);
    }

    private ExtractableResponse<Response> modifyLine(String lineId, Map<String, String> bodyParams) {
        return sendPut(bodyParams, API_URL_LINES + "/{id}", lineId);
    }

    private ExtractableResponse<Response> removeLine(String lineId) {
        return sendDelete(API_URL_LINES + "/{id}", lineId);
    }
}
