package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineResponse;
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

    public static final String ENDPOINT = "/lines";
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
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void 지하철_노선_생성_조회() {
        // when
        ExtractableResponse<Response> response = createLine("분당선", "노란색");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> lineNames = fetchLinesByPath("name", String.class);
        assertThat(lineNames).containsAnyOf("분당선");
        List<String> lineColors = fetchLinesByPath("color", String.class);
        assertThat(lineColors).containsAnyOf("노란색");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void 지하철_노선_목록_조회() {
        // when
        createLine("분당선", "노란색");
        createLine("3호선", "주황색");

        // when
        List<String> lineNames = fetchLinesByPath("name", String.class);

        // then
        assertThat(lineNames).containsExactly("분당선", "3호선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선 하나의 정보를 조회한다.")
    @Test
    void 지하철_노선_조회() {
        // when
        ExtractableResponse<Response> response = createLine("분당선", "노란색");
        Long lineId = response.body().jsonPath().getLong("id");

        // when
        LineResponse line = getLine(lineId);

        // then
        assertThat(line.getName()).isEqualTo("분당선");
        assertThat(line.getColor()).isEqualTo("노란색");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    void 지하철_노선_수정() {
        ExtractableResponse<Response> response = createLine("분당선", "노란색");
        Long lineId = response.body().jsonPath().getLong("id");

        Map<String, String> params = new HashMap<>();
        params.put("name", "구분당선");
        params.put("color", "노란색");

        editLine(lineId, params);

        LineResponse line = getLine(lineId);
        assertThat(line.getName()).isEqualTo("구분당선");
    }

    private void editLine(Long lineId, Map<String, String> params) {
        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(ENDPOINT + "/" + lineId)
                .then().log().all();
    }

    private ExtractableResponse<Response> createLine(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(ENDPOINT)
                .then().log().all()
                .extract();
    }

    private <T> List<T> fetchLinesByPath(String path, Class<T> genericType) {
        return RestAssured.given().log().all()
                .when().get(ENDPOINT)
                .then().log().all()
                .extract().jsonPath().getList(path, genericType);
    }

    private LineResponse getLine(Long lineId) {
        return RestAssured.given().log().all()
                .when().get(ENDPOINT + "/" + lineId)
                .then().log().all()
                .extract().jsonPath().getObject("", LineResponse.class);
    }
}
