package nextstep.subway.subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private Long upStationId;
    private Long downStationId;
    private Long lineId;

    @BeforeEach
    public void setUp() {
        super.setUp();
        upStationId = extractId(지하철역_등록("강남역"));
        downStationId = extractId(지하철역_등록("판교역"));
        Map<String, Object> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", 10);
        lineId = extractId(지하철노선_등록(params));
    }

    private ExtractableResponse<Response> getLines() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void 지하철노선_생성후_조회() {
        // when
        // then
        List<String> stationNames = extractList(getLines(), "name", String.class);
        assertThat(stationNames).containsAnyOf("신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void 지하철노선_목록_조회() {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "bg-green-600");
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", 10);
        지하철노선_등록(params);

        // when
        List<Long> ids = extractList(getLines(), "id", Long.class);

        // then
        assertThat(ids).hasSize(2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선을 조회한다.")
    @Test
    void 지하철노선_조회() {
        // give
        // when
        ExtractableResponse<Response> extract = 지하철노선_조회(lineId);

        // then
        assertThat(extractString(extract, "name")).isEqualTo("신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선을 수정한다.")
    @Test
    void 지하철노선_수정() {
        // given
        // when
        Map<String, String> reqBody = new HashMap<>();
        reqBody.put("name", "다른분당선");
        reqBody.put("color", "bg-green-600");
        ExtractableResponse<Response> updateResponse = RestAssured.given().log().all()
                .body(reqBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/{id}", lineId)
                .then().log().all()
                .extract();

        // then
        ExtractableResponse<Response> findResponse = 지하철노선_조회(lineId);
        assertAll(() -> assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(findResponse.body().jsonPath().getString("name")).isEqualTo("다른분당선"));
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선을 삭제한다.")
    @Test
    void 지하철노선_삭제() {
        // given
        // when
        ExtractableResponse<Response> deleteResponse = RestAssured.given().log().all()
                .when()
                .delete("/lines/{id}", lineId)
                .then().log().all()
                .extract();

        // then
        assertAll(() -> assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(getLines().body().jsonPath().getList("id")).isEmpty());
    }
}
