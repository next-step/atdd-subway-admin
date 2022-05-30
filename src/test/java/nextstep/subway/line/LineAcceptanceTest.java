package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.utils.RestAssuredMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

@DisplayName("지하철 노선 관련 기능")
@Sql("/truncate.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    String downStationId;
    String upStationId;


    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }

        upStationId = Integer.toString(지하철역_생성("강남역").jsonPath().get("id"));
        downStationId = Integer.toString(지하철역_생성("선릉역").jsonPath().get("id"));
    }

    /**
     * When 지하철 노선을 생성하면
     * <p>
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when

        ExtractableResponse<Response> response = 지하철노선_생성("신분당선", "bg-red-600", upStationId, downStationId, "10");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> lineNames = 지하철노선_목록_조회().getList("name");

        assertThat(lineNames).containsAnyOf("신분당선");
    }


    /**
     * Given 2개의 지하철 노선을 생성하고
     * <p>
     * When 지하철 노선 목록을 조회하면
     * <p>
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        지하철노선_생성("신분당선", "bg-red-600", upStationId, downStationId, "10");
        지하철노선_생성("분당선", "bg-green-600", upStationId, downStationId, "20");

        // when
        List<String> lineNames = 지하철노선_목록_조회().getList("name");

        // then
        assertThat(lineNames).containsExactly("신분당선", "분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * <p>
     * When 생성한 노선을 조회하면
     * <p>
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        Integer id = 지하철노선_생성("신분당선", "bg-red-600", upStationId, downStationId, "10").jsonPath().get("id");

        // when
        String lineName = 지하철노선_한개_조회(id).get("name");

        // then
        assertThat(lineName).isEqualTo("신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * <p>
     * When 생성한 지하철 노선을 수정하면
     * <p>
     * Then 해당 지하철 노선 정보는 수정된다.
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        Integer id = 지하철노선_생성("신분당선", "bg-red-600", upStationId, downStationId, "10").jsonPath().get("id");

        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선2");
        params.put("color", "bg-red-600");

        RestAssuredMethod.put("/lines/{id}", new HashMap<String, Integer>() {{
            put("id", id);
        }}, params);

        // then
        JsonPath jsonPath = 지하철노선_한개_조회(id);

        assertAll(
                () -> assertThat(jsonPath.getString("name")).isEqualTo("신분당선2"),
                () -> assertThat(jsonPath.getString("color")).isEqualTo("bg-red-600")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * <p>
     * When 생성한 지하철 노선을 삭제하면
     * <p>
     * Then 해당 지하철 노선 정보는 삭제된다.
     */
    @DisplayName("지하철노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        Integer id = 지하철노선_생성("신분당선", "bg-red-600", upStationId, downStationId, "10").jsonPath().get("id");

        // when
        ExtractableResponse<Response> response = RestAssuredMethod.delete("/lines/{id}",
                new HashMap<String, Integer>() {{
                    put("id", id);
                }});

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(지하철노선_목록_조회().getList("name")).doesNotContain("신분당선")
        );
    }

    private ExtractableResponse<Response> 지하철노선_생성(String name, String color, String upStationId, String downStationId,
                                                   String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssuredMethod.post("/lines", params);
    }

    private JsonPath 지하철노선_목록_조회() {
        return RestAssuredMethod.get("/lines").jsonPath();
    }

    private JsonPath 지하철노선_한개_조회(Integer id) {
        return RestAssuredMethod.get("/lines/{id}", new HashMap<String, Integer>() {{
            put("id", id);
        }}).jsonPath();
    }

    private ExtractableResponse<Response> 지하철역_생성(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        return RestAssuredMethod.post("/stations", params);
    }
}
