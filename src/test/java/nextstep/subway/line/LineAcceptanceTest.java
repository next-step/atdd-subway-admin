package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.common.RestAssuredTemplate;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.station.StationAcceptanceTest.지하철역_생성됨;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends BaseAcceptanceTest {

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    void 지하철노선_생성() {
        // given
        String name = "신분당선";
        Long 지하철역_id = 지하철역_생성됨("지하철역").body().jsonPath().getLong("id");
        Long 새로운지하철역_id = 지하철역_생성됨("새로운지하철역").body().jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성됨(name, "bg-red-600", 10, 지하철역_id, 새로운지하철역_id);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> lineNames = 지하철_노선_전체_조회();
        assertThat(lineNames).containsAnyOf(name);
    }


    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    void 지하철노선_목록_조회() {
        // given
        Long 지하철역_id = 지하철역_생성됨("지하철역").body().jsonPath().getLong("id");
        Long 새로운지하철역_id = 지하철역_생성됨("새로운지하철역").body().jsonPath().getLong("id");
        Long 또다른지하철역_id = 지하철역_생성됨("또다른지하철역").body().jsonPath().getLong("id");

        지하철_노선_생성됨("신분당선", "bg-red-600", 10, 지하철역_id, 새로운지하철역_id);
        지하철_노선_생성됨("분당선", "bg-green-600", 10, 지하철역_id, 또다른지하철역_id);

        // when
        List<String> lineNames = 지하철_노선_전체_조회();

        // then
        assertThat(lineNames).containsExactly("신분당선", "분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    void 지하철노선_조회() {
        // given
        Long 지하철역_id = 지하철역_생성됨("지하철역").body().jsonPath().getLong("id");
        Long 새로운지하철역_id = 지하철역_생성됨("새로운지하철역").body().jsonPath().getLong("id");

        String name = "신분당선";
        ExtractableResponse<Response> response = 지하철_노선_생성됨(name, "bg-red-600", 10, 지하철역_id, 새로운지하철역_id);
        long lineId = response.body().jsonPath().getLong("id");

        // when
        String lineName = 지하철_노선_조회(lineId);

        // then
        assertThat(lineName).isEqualTo(name);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    void 지하철노선_수정() {
        // given
        Long 지하철역_id = 지하철역_생성됨("지하철역").body().jsonPath().getLong("id");
        Long 새로운지하철역_id = 지하철역_생성됨("새로운지하철역").body().jsonPath().getLong("id");

        ExtractableResponse<Response> createResponse = 지하철_노선_생성됨("신분당선", "bg-green-600", 10, 지하철역_id, 새로운지하철역_id);
        long lineId = createResponse.body().jsonPath().getLong("id");

        // when
        String name = "다른분당선";
        String color = "bg-red-600";
        ExtractableResponse<Response> response = 지하철_노선_수정(lineId, name, color);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    void 지하철노선_삭제() {

    }

    public static ExtractableResponse<Response> 지하철_노선_생성됨(String name, String color, Integer distance, Long upStationId, Long downStationId) {
        return RestAssuredTemplate.post("/lines", new LineRequest(name, color, upStationId, downStationId, distance));
    }

    public static List<String> 지하철_노선_전체_조회() {
        return RestAssuredTemplate.get("/lines").body().jsonPath().getList("name", String.class);
    }

    public static String 지하철_노선_조회(long lineId) {
        return RestAssuredTemplate.get("/lines/" + lineId).jsonPath().getString("name");
    }

    public static ExtractableResponse<Response> 지하철_노선_수정(long lineId, String name, String color) {
        return RestAssuredTemplate.put("/lines/" + lineId, new LineUpdateRequest(name, color));
    }

}