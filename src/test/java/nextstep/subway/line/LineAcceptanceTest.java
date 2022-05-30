package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    static final String URL_PATH_LINES = "/lines";
    static final String KEY_ID = "id";
    static final String KEY_NAME = "name";
    static final String KEY_COLOR = "color";

    @LocalServerPort
    int port;
    LineRequest lineRequest1, lineRequest2;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        lineRequest1 = new LineRequest("신분당선", "bg-red-600");
        lineRequest2 = new LineRequest("2호선", "bg-green-600");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = createLine(lineRequest1);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        assertThat(getLineNames()).containsAnyOf(lineRequest1.getName());
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        createLine(lineRequest1);
        createLine(lineRequest2);

        // when
        List<String> lineNames = getLineNames();

        // then
        assertThat(lineNames).containsAnyOf(lineRequest1.getName(), lineRequest2.getName());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        long lineId = createLine(lineRequest1).jsonPath().getLong(KEY_ID);

        // when
        ExtractableResponse<Response> response = findByLineId(lineId);

        // then
        assertThat(response.jsonPath().getString(KEY_NAME)).isEqualTo(lineRequest1.getName());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        long lineId = createLine(lineRequest1).jsonPath().getLong(KEY_ID);

        // when
        LineRequest updateLineRequest = new LineRequest("뉴분당선", "bg-red-600");
        ExtractableResponse<Response> response = updateLine(lineId, updateLineRequest);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        assertThat(response.jsonPath().getString(KEY_NAME)).isEqualTo(updateLineRequest.getName());
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
        long lineId = createLine(lineRequest1).jsonPath().getLong(KEY_ID);

        // when
        assertThat(deleteLine(lineId).statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // then
        assertThat(getLineNames()).doesNotContain(lineRequest1.getName());

    }

    private List<String> getLineNames() {
        return RestAssured.given().log().all()
                .when().get(URL_PATH_LINES)
                .then().log().all()
                .extract().jsonPath().getList(KEY_NAME, String.class);
    }

    private ExtractableResponse<Response> findByLineId(long lineId) {
        return RestAssured.given().log().all()
                .when().get(URL_PATH_LINES + "/" + lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createLine(LineRequest request) {
        Map<String, String> params = new HashMap<>();
        params.put(KEY_NAME, request.getName());
        params.put(KEY_COLOR, request.getColor());
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(URL_PATH_LINES)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> updateLine(long lineId, LineRequest request) {
        Map<String, String> params = new HashMap<>();
        params.put(KEY_NAME, request.getName());
        params.put(KEY_COLOR, request.getColor());
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(URL_PATH_LINES + "/" + lineId)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> deleteLine(long lineId) {
        return RestAssured.given().log().all()
                .when().delete(URL_PATH_LINES + "/" + lineId)
                .then().log().all().extract();
    }

    private static class LineRequest {
        private final String name;
        private final String color;

        public LineRequest(String name, String color) {
            this.name = name;
            this.color = color;
        }

        public String getName() {
            return this.name;
        }

        public String getColor() {
            return this.color;
        }
    }
}
