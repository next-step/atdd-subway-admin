package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.dto.LineUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@Sql("classpath:/createstation.sql")
public class LineAcceptanceTest extends BaseAcceptanceTest {

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철노선 생성")
    @Test
    void createLine() {
        // When
        ExtractableResponse<Response> 신분당선_생성_응답 = 신분당선_생성();
        assertThat(신분당선_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // Then
        ExtractableResponse<Response> 목록_조회_결과_응답 = 노선_목록_조회();
        assertThat(목록_조회_결과_응답.jsonPath().getList(".")).hasSize(1);
    }

    /**
     * Given 존재하지 않는 역 정보를 이용하여
     * When 지하철 노선을 생성하면
     * Then 오류 메시지와 함께 노선이 생성되지 않는다.
     */
    @DisplayName("존재하지 않는 역을 이용한 지하철노선 생성")
    @Test
    void createLineWithNotSavedStation() {
        // Given
        final long 미존재_상행역_ID = 100L;
        final long 미존재_하행역_ID = 101L;

        Map<String, Object> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", 미존재_상행역_ID);
        params.put("downStationId", 미존재_하행역_ID);
        params.put("distance", 10);

        // when
        ExtractableResponse<Response> 노선_생성_에러_응답 = 노선_생성(params);

        // Then
        assertThat(노선_생성_에러_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록 조회")
    @Test
    void showLines() {
        // Given
        신분당선_생성();
        신림선_생성();

        // When
        ExtractableResponse<Response> 노선_목록_조회_결과 = 노선_목록_조회();

        // Then
        assertThat(노선_목록_조회_결과.jsonPath().getList("name")).hasSize(2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선 조회")
    @Test
    void showLine() {
        // Given
        ExtractableResponse<Response> 신분당선_생성_응답 = 신분당선_생성();

        // When
        Integer 신분당선_ID = 신분당선_생성_응답.jsonPath().get("id");
        ExtractableResponse<Response> 노선_조회_결과 = 노선_조회(Long.valueOf(신분당선_ID));

        // Then
        assertThat(노선_조회_결과.jsonPath().getString("name")).isEqualTo("신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다.
     */
    @DisplayName("지하철노선 수정")
    @Test
    void updateLine() {
        // Given
        ExtractableResponse<Response> 신분당선_생성_응답 = 신분당선_생성();

        // When
        Long 신분당선_ID = Long.valueOf((Integer) 신분당선_생성_응답.jsonPath().get("id"));
        LineUpdateRequest lineUpdateRequest = new LineUpdateRequest( "다른분당선", "bg-100-1234");
        ExtractableResponse<Response> 노선_수정_결과 = 노선_수정(신분당선_ID, lineUpdateRequest);

        // Then
        assertThat(노선_수정_결과.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * When 존재하지 않는 지하철 노선을 수정하려고 하는 경우
     * Then 오류가 발생한다.
     */
    @DisplayName("존재하지 않는 지하철노선 수정")
    @Test
    void updateLineWithNotSavedLine() {
        // When
        long 존재하지_않는_노선_ID = 100L;
        LineUpdateRequest lineUpdateRequest = new LineUpdateRequest( "다른분당선", "bg-100-1234");
        ExtractableResponse<Response> 노선_수정_결과 = 노선_수정(존재하지_않는_노선_ID, lineUpdateRequest);

        // Then
        assertThat(노선_수정_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선 삭제")
    @Test
    void deleteLine() {
        // Given
        ExtractableResponse<Response> 신분당선_생성_응답 = 신분당선_생성();

        // When
        Long 신분당선_ID = Long.valueOf((Integer) 신분당선_생성_응답.jsonPath().get("id"));
        노선_삭제(신분당선_ID);

        // Then
        assertThat(노선_조회(신분당선_ID).jsonPath().getString("id")).isNull();
    }

    private ExtractableResponse<Response> 노선_삭제(Long id) {
        return RestAssured
                .given().log().all()
                .when().delete("/lines/" + id)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> 노선_수정(Long id, LineUpdateRequest lineUpdateRequest) {
        return RestAssured
                .given().log().all()
                .body(lineUpdateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/" + id)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> 노선_조회(Long id) {
        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/" + id)
                .then().log().all().extract();
    }

    protected static ExtractableResponse<Response> 노선_목록_조회() {
        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all().extract();
    }

    protected static ExtractableResponse<Response> 노선_생성(Map<String, Object> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 신분당선_생성() {
        final long 강남역_ID = 1L;
        final long 양재역_ID = 2L;

        Map<String, Object> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", 강남역_ID);
        params.put("downStationId", 양재역_ID);
        params.put("distance", 10);

        return 노선_생성(params);
    }

    private ExtractableResponse<Response> 신림선_생성() {
        final long 신림역_ID = 3L;
        final long 서원역_ID = 4L;

        Map<String, Object> params = new HashMap<>();
        params.put("name", "신림선");
        params.put("color", "bg-red-100");
        params.put("upStationId", 신림역_ID);
        params.put("downStationId", 서원역_ID);
        params.put("distance", 7);

        return 노선_생성(params);
    }
}
