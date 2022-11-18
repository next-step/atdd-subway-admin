package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import nextstep.subway.utils.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class SectionAcceptanceTest extends BaseTest {
    /**
     * Given
     * When 지하철_노선에_지하철역_등록_요청
     * Then 지하철_노선에_지하철역_등록됨
     */
    @Test
    void 역_사이에_새로운_역을_등록할_경우() {
        ExtractableResponse<Response> response = createLineStation(신분당선.getId(), 강남역.getId(), 판교역.getId(), 4);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /**
     * Given
     * When 지하철_노선에_지하철역_등록_요청
     * Then 지하철_노선에_지하철역_등록됨
     */
    @Test
    void 새로운_역을_상행_종점으로_등록할_경우() {
        ExtractableResponse<Response> response = createLineStation(신분당선.getId(), 판교역.getId(), 강남역.getId(), 4);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /**
     * Given
     * When 지하철_노선에_지하철역_등록_요청
     * Then 지하철_노선에_지하철역_등록됨
     */
    @Test
    void 새로운_역을_하행_종점으로_등록할_경우() {
        ExtractableResponse<Response> response = createLineStation(신분당선.getId(), 광교역.getId(), 판교역.getId(), 4);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /**
     * Given
     * When 지하철 노선에 지하철역 등록 요청
     * Then 500 Internal Server Error 발생
     */
    @Test
    void 역_사이에_새로운_역을_등록할_경우_기존_역_사이_길이보다_크거나_같으면_등록을_할_수_없음() {
        ExtractableResponse<Response> response = createLineStation(신분당선.getId(), 강남역.getId(), 판교역.getId(), 10);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * Given
     * When 지하철 노선에 지하철역 등록 요청
     * Then 500 Internal Server Error 발생
     */
    @Test
    void 상행역과_하행역이_이미_노선에_모두_등록되어_있다면_추가할_수_없음() {
        ExtractableResponse<Response> response = createLineStation(신분당선.getId(), 강남역.getId(), 광교역.getId(), 4);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * Given
     * When 지하철 노선에 지하철역 등록 요청
     * Then 500 Internal Server Error 발생
     */
    @Test
    void 상행역과_하행역_둘_중_하나도_포함되어있지_않으면_추가할_수_없음() {
        ExtractableResponse<Response> response = createLineStation(신분당선.getId(), 서울대입구역.getId(), 낙성대역.getId(), 3);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * Given 정자역 - 광교역, 강남역 - 판교역 구간 등록
     * When 지하철 노선 조회
     * Then 노선에 속한 지하철 역이 상행 ~ 하행으로 정렬
     */
    @Test
    void 노선_조회시_구간_정렬_확인() {
        createLineStation(신분당선.getId(), 정자역.getId(), 광교역.getId(), 2);
        createLineStation(신분당선.getId(), 강남역.getId(), 판교역.getId(), 2);

        ExtractableResponse<Response> response = getLine(신분당선.getId());
        assertAll(
            () -> assertThat(response.jsonPath().getString("stations[0].name")).isEqualTo("강남역"),
            () -> assertThat(response.jsonPath().getString("stations[1].name")).isEqualTo("판교역"),
            () -> assertThat(response.jsonPath().getString("stations[2].name")).isEqualTo("정자역"),
            () -> assertThat(response.jsonPath().getString("stations[3].name")).isEqualTo("광교역")
        );
    }

    private static ExtractableResponse<Response> createLineStation(Long lineId, Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/"+ lineId + "/sections")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> getLine(Long lineId) {
        return RestAssured.given().log().all()
                .when().get("/lines/" + lineId)
                .then().log().all()
                .extract();
    }
}
