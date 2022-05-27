package nextstep.subway.acceptance.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.acceptance.base.BaseAcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철노선 관련 기능")
class LineAcceptanceTest extends BaseAcceptanceTest {

    /**
     * Given 상행, 하행 지하철 역을 생성하고
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철노선 생성")
    @Test
    void createLine() {
        // Given
        지하철역_생성_요청("지하철역");
        지하철역_생성_요청("새로운지하철역");

        // when
        지하철노선_생성_요청("신분당선", "bg-red-600", 1, 2, 10);

        // then
        ExtractableResponse<Response> showResponse = 지하철노선_목록_조회_요청();
        List<String> lineNames = showResponse.jsonPath().getList("name", String.class);
        List<String> colors = showResponse.jsonPath().getList("color", String.class);
        List<String> stations = showResponse.jsonPath().getList("stations[0].name", String.class);
        assertAll(
                () -> assertThat(lineNames).containsAnyOf("신분당선"),
                () -> assertThat(colors).containsAnyOf("bg-red-600"),
                () -> assertThat(stations).containsExactly("지하철역","새로운지하철역")
        );
    }

    /**
     * Given 상행, 하행 지하철 역을 생성하고
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록 조회")
    @Test
    void showLines() {
        // Given
        지하철역_생성_요청("지하철역");
        지하철역_생성_요청("새로운지하철역");
        // Given
        지하철노선_생성_요청("신분당선", "bg-red-600", 1, 2, 10);
        지하철노선_생성_요청("5호선", "bg-blue-600", 1, 2, 10);

        // when
        ExtractableResponse<Response> showResponse = 지하철노선_목록_조회_요청();

        // then
        List<String> lineNames = showResponse.jsonPath().getList("name", String.class);
        List<String> colors = showResponse.jsonPath().getList("color", String.class);
        List<String> stations1 = showResponse.jsonPath().getList("stations[0].name", String.class);
        List<String> stations2 = showResponse.jsonPath().getList("stations[1].name", String.class);
        assertAll(
                () -> assertThat(lineNames).containsExactly("신분당선", "5호선"),
                () -> assertThat(lineNames).hasSize(2),
                () -> assertThat(colors).containsExactly("bg-red-600", "bg-blue-600"),
                () -> assertThat(stations1).containsExactly("지하철역", "새로운지하철역"),
                () -> assertThat(stations2).containsExactly("지하철역", "새로운지하철역")
        );
    }

    /**
     * Given 상행, 하행 지하철 역을 생성하고
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선 조회")
    @Test
    void showLine() {
        // Given
        지하철역_생성_요청("지하철역");
        지하철역_생성_요청("새로운지하철역");
        // Given
        long id = 지하철노선_생성_요청("신분당선", "bg-red-600", 1, 2, 10).jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> showResponse = 지하철노선_조회_요청(id);

        // then
        String lineNames = showResponse.jsonPath().getString("name");
        String colors = showResponse.jsonPath().getString("color");
        List<String> stations = showResponse.jsonPath().getList("stations.name");
        assertAll(
                () -> assertThat(lineNames).isEqualTo("신분당선"),
                () -> assertThat(colors).isEqualTo("bg-red-600"),
                () -> assertThat(stations).containsExactly("지하철역", "새로운지하철역")
        );
    }

    /**
     * Given 상행, 하행 지하철 역을 생성하고
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선 수정")
    @Test
    void updateLine() {
        // Given
        지하철역_생성_요청("지하철역");
        지하철역_생성_요청("새로운지하철역");
        // Given
        지하철노선_생성_요청("신분당선", "bg-red-600", 1, 2, 10);

        // when
        ExtractableResponse<Response> updateResponse = 지하철노선_변경_요청("신분당선", "bg-blue-600", 1, 2, 12);

        // then
        String lineNames = updateResponse.jsonPath().getString("name");
        String colors = updateResponse.jsonPath().getString("color");
        List<String> stations = updateResponse.jsonPath().getList("stations.name");
        assertAll(
                () -> assertThat(lineNames).isEqualTo("신분당선"),
                () -> assertThat(colors).isEqualTo("bg-blue-600"),
                () -> assertThat(stations).containsExactly("지하철역", "새로운지하철역")
        );
    }

    /**
     * Given 상행, 하행 지하철 역을 생성하고
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선 삭제")
    @Test
    void deleteLine() {
        // Given
        지하철역_생성_요청("지하철역");
        지하철역_생성_요청("새로운지하철역");

        // Given
        ExtractableResponse<Response> createResponse = 지하철노선_생성_요청("신분당선", "bg-red-600", 1, 2, 10);
        long id = createResponse.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> deleteResponse = 지하철노선_제거_요청(id);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    ExtractableResponse<Response> 지하철노선_생성_요청(String name, String color, long upStationId, long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    ExtractableResponse<Response> 지하철노선_목록_조회_요청() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    ExtractableResponse<Response> 지하철노선_조회_요청(long id) {
        return RestAssured.given().log().all()
                .when().get("/lines/" + id)
                .then().log().all()
                .extract();
    }

    ExtractableResponse<Response> 지하철노선_변경_요청(String name, String color, long upStationId, long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines")
                .then().log().all()
                .extract();
    }

    ExtractableResponse<Response> 지하철노선_제거_요청(long id) {
        return RestAssured.given().log().all()
                .when().delete("/lines/" + id)
                .then().log().all()
                .extract();
    }

    ExtractableResponse<Response> 지하철역_생성_요청(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }
}
