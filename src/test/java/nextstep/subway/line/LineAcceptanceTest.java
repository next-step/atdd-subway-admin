package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
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
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
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

    private ExtractableResponse<Response> 지하철노선_생성(String name, String color, String upStationId, String downStationId,
                                                   String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("/lines")
                        .then().log().all()
                        .extract();
        return response;
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
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when().get("/lines/{id}", id)
                .then().log().all()
                .extract();

        String lineName = response.jsonPath().get("name");

        // then
        assertThat(lineName).isEqualTo("신분당선");
    }

    private JsonPath 지하철노선_목록_조회() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath();

    }

    private ExtractableResponse<Response> 지하철역_생성(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("/stations")
                        .then().log().all()
                        .extract();
        return response;
    }
}
