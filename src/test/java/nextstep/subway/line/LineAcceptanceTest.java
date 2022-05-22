package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ResponseBodyExtractionOptions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.BaseAcceptanceTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class LineAcceptanceTest extends BaseAcceptanceTest {

    /**
     * When 지하철 노선을 생성하면 Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    public void createLine() {
        // given
        String 신분당선 = "신분당선";

        // when
        ExtractableResponse<Response> 노선_생성_응답
            = 노선_생성_요청(신분당선, "bg-red-600", 1, 2, 10);
        생성됨_확인(노선_생성_응답);

        //then
        ExtractableResponse<Response> 노선_목록 = 노선_목록_요청();
        노선_목록_확인(노선_목록, 신분당선);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고 When 지하철 노선 목록을 조회하면 Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    public void getLines() {
        // given
        String 신분당선 = "신분당선";
        노선_생성_요청(신분당선, "bg-red-600", 1, 2, 10);
        String 분당선 = "분당선";
        노선_생성_요청(분당선, "bg-green-600", 1, 3, 20);

        // when
        ExtractableResponse<Response> 노선_목록 = 노선_목록_요청();

        // then
        노선_목록_확인(노선_목록, 신분당선, 분당선);
    }


    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    public void getLine() {
        // given
        String 신분당선 = "신분당선";
        String color = "bg-red-600";
        long upStationId = 1;
        long downStationId = 2;
        int distance = 10;
        ExtractableResponse<Response> 노선_생성_응답
            = 노선_생성_요청(신분당선, color, upStationId, downStationId, distance);
        Long id = getId(노선_생성_응답);

        // when
        ExtractableResponse<Response> 노선_조회_응답 = 노선_조회_요청(id);

        // then
        노선_조회_확인(신분당선, color, upStationId, downStationId, distance, 노선_조회_응답);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    public void updateLine() {
        // given
        ExtractableResponse<Response> 노선_생성_응답
            = 노선_생성_요청("신분당선", "bg-red-600", 1, 2, 10);
        Long id = getId(노선_생성_응답);

        // when
        String 분당선 = "분당선";
        String color = "bg-green-600";
        long upStationId = 2;
        long downStationId = 3;
        int distance = 20;
        노선_수정_요청(id, 분당선, color, upStationId, downStationId, distance);

        // then
        ExtractableResponse<Response> 노선_조회_응답 = 노선_조회_요청(id);
        노선_조회_확인(분당선, color, upStationId, downStationId, distance, 노선_조회_응답);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    public void removeLine() {
        // given
        ExtractableResponse<Response> 생성_응답
            = 노선_생성_요청("신분당선", "bg-red-600", 1, 2, 10);
        Long id = getId(생성_응답);

        // when
        ExtractableResponse<Response> 삭제_응답 = 노선_삭제_요청(id);
        삭제됨_확인(삭제_응답);

        // then
        ExtractableResponse<Response> 노선_목록_응답 = 노선_목록_요청();
        이름_불포함_확인(노선_목록_응답);
    }

    private ExtractableResponse<Response> 노선_생성_요청(String name, String color, long upStationId, long downStationId,
        int distance) {
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

    private ExtractableResponse<Response> 노선_목록_요청() {
        return RestAssured.given().log().all()
            .when().get("/lines")
            .then().log().all()
            .extract();
    }

    private void 노선_목록_확인(ExtractableResponse<Response> response, String... lineNames) {
        List<String> requestLineNames = response.body().jsonPath().getList("name", String.class);
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.statusCode()),
            () -> assertThat(lineNames).containsAll(requestLineNames)
        );
    }

    private ExtractableResponse<Response> 노선_조회_요청(Long id) {
        return RestAssured.given().log().all()
            .when().get("/lines/{id}", id)
            .then().log().all()
            .extract();
    }

    private long getId(ExtractableResponse<Response> 노선_생성_응답) {
        return 노선_생성_응답.body().jsonPath().getLong("id");
    }

    private void 노선_조회_확인(String 신분당선, String color, long upStationId, long downStationId, int distance,
        ExtractableResponse<Response> 노선_조회_응답) {

        ResponseBodyExtractionOptions body = 노선_조회_응답.body();
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), 노선_조회_응답.statusCode()),
            () -> assertThat(body.jsonPath().getLong("id")).isNotNull(),
            () -> assertEquals(신분당선, upStationId, body.jsonPath().getString("name")),
            () -> assertEquals(color, upStationId, body.jsonPath().getString("color")),
            () -> assertEquals(upStationId, upStationId, body.jsonPath().getLong("upStationId")),
            () -> assertEquals(downStationId, upStationId, body.jsonPath().getLong("downStationId")),
            () -> assertEquals(distance, upStationId, body.jsonPath().getLong("distance"))
        );
    }

    private ExtractableResponse<Response> 노선_수정_요청(Long id, String name, String color, long upStationId,
        long downStationId, int distance) {

        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured.given().log().all()
            .when().put("/lines/{id}", id)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 노선_삭제_요청(Long id) {
        return RestAssured.given().log().all()
            .when().delete()
            .then().log().all()
            .extract();
    }

    private void 삭제됨_확인(ExtractableResponse<Response> 삭제_응답) {
        assertEquals(HttpStatus.NO_CONTENT.value(), 삭제_응답.statusCode());
    }

}
