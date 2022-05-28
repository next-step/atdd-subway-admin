package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    /**
     * Given 지하철역을 두개 상성하고
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    void 지하철_노선을_생성한다() {
        StationAcceptanceTest.지하철역을_생성("소요산");
        StationAcceptanceTest.지하철역을_생성("신창");

        ExtractableResponse<Response> 파란색_1호선 = 지하철노선_생성("1호선", "blue darken-4", 1L, 2L);

        assertThat(파란색_1호선.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    void 지하철노선_목록을_조회한다() {
        지하철노선_생성("1호선", "소요산역", "신창역");
        지하철노선_생성("7호선", "장암역", "석남역");

        ExtractableResponse<Response> 지하철노선_목록 = RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();

        assertThat(지하철노선_목록.jsonPath().getList("name", String.class).size()).isEqualTo(2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    void 지하철노선을_조회한다() {
        String lineName = "1호선";
        Long id = 지하철노선_생성(lineName, "소요산역", "신창역")
                .jsonPath().getObject("id", Long.class);

        LineResponse 지하철노선_1호선 = RestAssured
                .given().log().all()
                .when().get("/lines/" + id)
                .then().log().all()
                .extract()
                .jsonPath().getObject(".", LineResponse.class);

        assertThat(지하철노선_1호선.getName()).isEqualTo(lineName);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    void 지하철노선을_수정한다() {
        Long id = 지하철노선_생성("1호선", "소요산역", "신창역")
                .jsonPath().getObject("id", Long.class);

        Map<String, Object> 분당선_정보 = new HashMap<>();
        분당선_정보.put("name", "분당선");
        분당선_정보.put("color", "노란색");

        ExtractableResponse<Response> 수정결과 = RestAssured
                .given().log().all()
                .body(분당선_정보)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/" + id)
                .then().log().all()
                .extract();

        assertThat(수정결과.statusCode()).isEqualTo(HttpStatus.OK.value());
    }


    private ExtractableResponse<Response> 지하철노선_생성(String lineName, String upStationName, String downStationName) {
        Map<String, Object> params = 지하철노선_정보_생성(lineName, upStationName, downStationName);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철노선_생성(String name, String color, Long upStationId, Long downStationId) {
        Map<String, Object> param = new HashMap<>();
        param.put("name", name);
        param.put("color", color);
        param.put("upStationId", upStationId);
        param.put("downStationId", downStationId);

        return RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private static Map<String, Object> 지하철노선_정보_생성(String lineName, String upStationName, String downStationName) {
        Long upStationId = StationAcceptanceTest.지하철역을_생성(upStationName)
                .jsonPath().getObject("id", Long.class);

        Long downStationId = StationAcceptanceTest.지하철역을_생성(downStationName)
                .jsonPath().getObject("id", Long.class);

        Map<String, Object> params = new HashMap<>();
        params.put("name", lineName);
        params.put("color", "색상");
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", 10);

        return params;
    }


}
