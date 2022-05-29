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
import java.util.Map;

import static nextstep.subway.line.LineAcceptanceFactory.ID값으로_지하철노선_조회;
import static nextstep.subway.line.LineAcceptanceFactory.지하철노선_목록_조회;
import static nextstep.subway.line.LineAcceptanceFactory.지하철노선_생성;
import static nextstep.subway.station.StationAcceptanceFactory.지하철역_생성;
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
        지하철역_생성("소요산");
        지하철역_생성("신창");

        Map<String, Object> param = new HashMap<>();
        param.put("name", "1호선");
        param.put("color", "blue darken-4");
        param.put("upStationId", 1L);
        param.put("downStationId", 2L);
        param.put("distance", 10);

        ExtractableResponse<Response> 파란색_1호선 = RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

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

        ExtractableResponse<Response> 지하철노선_목록 = 지하철노선_목록_조회();

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

        LineResponse 지하철노선_1호선 = ID값으로_지하철노선_조회(id);

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

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    void 지하철노선을_삭제한다() {
        Long id = 지하철노선_생성("1호선", "소요산역", "신창역")
                .jsonPath().getObject("id", Long.class);

        ExtractableResponse<Response> 삭제결과 = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/" + id)
                .then().log().all()
                .extract();

        assertThat(삭제결과.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
