package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.StationAcceptanceTest;
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
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        String expectLine = "3호선";
        Long upStationId = StationAcceptanceTest.createStation("연신내역")
                .jsonPath().getLong("id");
        Long downStationId = StationAcceptanceTest.createStation("불광역")
                .jsonPath().getLong("id");

        ExtractableResponse<Response> response =
                createLine(expectLine, "주황색", upStationId, downStationId, 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> lineNames = readLines()
                .jsonPath().getList("name", String.class);
        assertThat(lineNames).containsAnyOf(expectLine);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void showLines() {
        // given
        String expectLine1 = "3호선";
        String expectLine2 = "분당선";

        Long upStationId = StationAcceptanceTest.createStation("연신내역")
                .jsonPath().getLong("id");
        Long downStationId = StationAcceptanceTest.createStation("불광역")
                .jsonPath().getLong("id");

        createLine(expectLine1, "주황색", upStationId, downStationId, 10);
        createLine(expectLine2, "노랑색", upStationId, downStationId, 10);

        // when
        List<String> lineNames = readLines()
                .jsonPath().getList("name", String.class);

        // then
        assertThat(lineNames).contains(expectLine1, expectLine2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void showLine() {
        // given
        String expectLine = "3호선";
        Long upStationId = StationAcceptanceTest.createStation("연신내역")
                .jsonPath().getLong("id");
        Long downStationId = StationAcceptanceTest.createStation("불광역")
                .jsonPath().getLong("id");

        Long lineId = createLine(expectLine, "주황색", upStationId, downStationId, 10)
                .jsonPath().getLong("id");

        // when
        String result = readLine(lineId)
                .jsonPath().getString("name");

        // then
        assertThat(result).isEqualTo(expectLine);
    }

    private ExtractableResponse<Response> createLine(
            String name,
            String color,
            Long upStationId,
            Long downStationId,
            int distance
    ) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId.toString());
        params.put("downStationId", downStationId.toString());
        params.put("distance", String.valueOf(distance));

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> updateLine(Long id, String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().patch("/lines/" + id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> deleteLine(Long id) {
        return RestAssured.given().log().all()
                .when().delete("/lines/" + id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> readLines() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> readLine(Long id) {
        return RestAssured.given().log().all()
                .when().get("/lines/" + id)
                .then().log().all()
                .extract();
    }
}
