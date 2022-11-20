package nextstep.subway.line;

import static nextstep.subway.constant.Constant.ADD_SECTION_FAIL_CAUSE_DISTANCE;
import static nextstep.subway.constant.Constant.ADD_SECTION_FAIL_CAUSE_DUPLICATE;
import static nextstep.subway.constant.Constant.ADD_SECTION_FAIL_CAUSE_NOT_MATCH;
import static nextstep.subway.constant.Constant.DELETE_FAIL_CAUSE_ONLY_ONE;
import static nextstep.subway.constant.Constant.NOT_FOUND_SECTION;
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
        ExtractableResponse<Response> response = createSection(신분당선.getId(), 강남역.getId(), 판교역.getId(), 4);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /**
     * Given
     * When 지하철_노선에_지하철역_등록_요청
     * Then 지하철_노선에_지하철역_등록됨
     */
    @Test
    void 새로운_역을_상행_종점으로_등록할_경우() {
        ExtractableResponse<Response> response = createSection(신분당선.getId(), 판교역.getId(), 강남역.getId(), 4);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /**
     * Given
     * When 지하철_노선에_지하철역_등록_요청
     * Then 지하철_노선에_지하철역_등록됨
     */
    @Test
    void 새로운_역을_하행_종점으로_등록할_경우() {
        ExtractableResponse<Response> response = createSection(신분당선.getId(), 광교역.getId(), 판교역.getId(), 4);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /**
     * Given
     * When 지하철 노선에 지하철역 등록 요청
     * Then 등록 불가능
     */
    @Test
    void 역_사이에_새로운_역을_등록할_경우_기존_역_사이_길이보다_크거나_같으면_등록을_할_수_없음() {
        ExtractableResponse<Response> response = createSection(신분당선.getId(), 강남역.getId(), 판교역.getId(), 10);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.jsonPath().getInt("httpStatus")).isEqualTo(400),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo(ADD_SECTION_FAIL_CAUSE_DISTANCE)
        );
    }

    /**
     * Given
     * When 지하철 노선에 지하철역 등록 요청
     * Then 등록 불가능
     */
    @Test
    void 상행역과_하행역이_이미_노선에_모두_등록되어_있다면_추가할_수_없음() {
        ExtractableResponse<Response> response = createSection(신분당선.getId(), 강남역.getId(), 광교역.getId(), 4);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.jsonPath().getInt("httpStatus")).isEqualTo(400),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo(ADD_SECTION_FAIL_CAUSE_DUPLICATE)
        );
    }

    /**
     * Given
     * When 지하철 노선에 지하철역 등록 요청
     * Then 등록 불가능
     */
    @Test
    void 상행역과_하행역_둘_중_하나도_포함되어있지_않으면_추가할_수_없음() {
        ExtractableResponse<Response> response = createSection(신분당선.getId(), 서울대입구역.getId(), 낙성대역.getId(), 3);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.jsonPath().getInt("httpStatus")).isEqualTo(400),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo(ADD_SECTION_FAIL_CAUSE_NOT_MATCH)
        );
    }

    /**
     * Given 정자역 - 광교역, 강남역 - 판교역 구간 등록
     * When 지하철 노선 조회
     * Then 노선에 속한 지하철 역이 상행 ~ 하행으로 정렬
     */
    @Test
    void 노선_조회시_구간_정렬_확인() {
        createSection(신분당선.getId(), 정자역.getId(), 광교역.getId(), 2);
        createSection(신분당선.getId(), 강남역.getId(), 판교역.getId(), 2);

        ExtractableResponse<Response> response = getLine(신분당선.getId());
        assertAll(
            () -> assertThat(response.jsonPath().getString("stations[0].name")).isEqualTo("강남역"),
            () -> assertThat(response.jsonPath().getString("stations[1].name")).isEqualTo("판교역"),
            () -> assertThat(response.jsonPath().getString("stations[2].name")).isEqualTo("정자역"),
            () -> assertThat(response.jsonPath().getString("stations[3].name")).isEqualTo("광교역")
        );
    }

    /**
     * Given 강남역 - 판교역 구간 등록
     * When 종점 제거
     * Then 강남역 - 판교역 구간 조회
     */
    @Test
    void 종점을_제거하는_경우() {
        createSection(신분당선.getId(), 강남역.getId(), 판교역.getId(), 4);

        ExtractableResponse<Response> deleteResponse = removeSection(신분당선.getId(), 광교역.getId());
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> getResponse = getLine(신분당선.getId());
        assertAll(
            () -> assertThat(getResponse.jsonPath().getString("stations[0].name")).isEqualTo("강남역"),
            () -> assertThat(getResponse.jsonPath().getString("stations[1].name")).isEqualTo("판교역")
        );
    }

    /**
     * Given 강남역 - 판교역 구간 등록
     * When 가운데 역 제거
     * Then 강남역 - 광교역 구간 조회
     */
    @Test
    void 가운데_역을_제거하는_경우() {
        createSection(신분당선.getId(), 강남역.getId(), 판교역.getId(), 4);

        ExtractableResponse<Response> deleteResponse = removeSection(신분당선.getId(), 판교역.getId());
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> getResponse = getLine(신분당선.getId());
        assertAll(
            () -> assertThat(getResponse.jsonPath().getString("stations[0].name")).isEqualTo("강남역"),
            () -> assertThat(getResponse.jsonPath().getString("stations[1].name")).isEqualTo("광교역")
        );
    }

    /**
     * Given 강남역 - 판교역, 판교역 - 광교역 구간
     * When 노선에 등록되어 있지 않는 역을 제거
     * Then 제거 불가능
     */
    @Test
    void 노선에_등록되어있지_않은_역을_제거할_때() {
        createSection(신분당선.getId(), 강남역.getId(), 판교역.getId(), 4);

        ExtractableResponse<Response> response = removeSection(신분당선.getId(), 서울대입구역.getId());

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.jsonPath().getInt("httpStatus")).isEqualTo(400),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo(NOT_FOUND_SECTION)
        );
    }


    /**
     * Given
     * When 구간이 하나인 노선에서 역을 제거
     * Then 제거 불가능
     */
    @Test
    void 구간이_하나인_노선에서_역을_제거하는_경우() {
        ExtractableResponse<Response> response = removeSection(신분당선.getId(), 강남역.getId());

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.jsonPath().getInt("httpStatus")).isEqualTo(400),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo(DELETE_FAIL_CAUSE_ONLY_ONE)
        );
    }

    private static ExtractableResponse<Response> createSection(Long lineId, Long upStationId, Long downStationId, int distance) {
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

    private static ExtractableResponse<Response> removeSection(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .when().delete("/lines/" + lineId + "/sections?stationId=" + stationId)
                .then().log().all()
                .extract();
    }
}
