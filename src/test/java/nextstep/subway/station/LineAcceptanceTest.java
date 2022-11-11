package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철노선 관련 기능")
public class LineAcceptanceTest extends AbstractAcceptanceTest {

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철노선이 생성된다
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Long upStationId = 지하철역_등록_요청("강남역").jsonPath().getLong("id");
        Long downStationId = 지하철역_등록_요청("역삼역").jsonPath().getLong("id");
        Long distance = 6L;

        // when
        String lineName = "2호선";
        String lineColor = "bg-green";
        ExtractableResponse<Response> response = 지하철노선_등록_요청(lineName, lineColor, upStationId, downStationId, distance);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> lineNames = 지하철노선_목록_조회_요청().jsonPath().getList("name", String.class);
        assertThat(lineNames).containsAnyOf(lineName);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다
     */
    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        Long upStationId = 지하철역_등록_요청("당고개역").jsonPath().getLong("id");
        Long downStationId = 지하철역_등록_요청("노원역").jsonPath().getLong("id");
        Long distance = 10L;
        지하철노선_등록_요청("4호선", "bg-blue", upStationId, downStationId, distance);
        지하철노선_등록_요청("7호선", "bg-khaki", upStationId, downStationId, distance);

        // when
        ExtractableResponse<Response> response = 지하철노선_목록_조회_요청();

        // then
        assertThat(response.jsonPath().getList("name", String.class)).hasSize(2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다
     */
    @DisplayName("지하철노선을 조회한다.")
    @Test
    void getLine() {
        // given
        Long upStationId = 지하철역_등록_요청("상수역").jsonPath().getLong("id");
        Long downStationId = 지하철역_등록_요청("합정역").jsonPath().getLong("id");
        ExtractableResponse<Response> createResponse = 지하철노선_등록_요청("6호선", "bg-brown", upStationId, downStationId, 10L);

        // when
        ExtractableResponse<Response> getResponse = 지하철노선_조회_요청(createResponse.jsonPath().getLong("id"));

        // then
        assertAll(
                () -> assertThat(getResponse.jsonPath().getString("name")).isEqualTo("6호선"),
                () -> assertThat(getResponse.jsonPath().getString("color")).isEqualTo("bg-brown")
        );
    }

    ExtractableResponse<Response> 지하철노선_등록_요청(String name, String color, Long upStationId, Long downStationId, Long distance) {
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

    ExtractableResponse<Response> 지하철노선_조회_요청(Long id) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .when().get("/lines/{id}")
                .then().log().all()
                .extract();
    }

    ExtractableResponse<Response> 지하철역_등록_요청(String name) {
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
