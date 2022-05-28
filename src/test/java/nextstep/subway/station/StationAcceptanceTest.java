package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import nextstep.subway.common.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.station.StationAcceptanceTest.StationAcceptanceTemplate.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @Test
    void 지하철역을_생성한다() {
        // when
        ExtractableResponse<Response> 강남역 = 지하철역_생성("강남역");

        // then
        지하철역_생성_성공을_확인한다(강남역);

        // then
        List<String> 지하철역_목록 = 지하철역_목록_조회();
        지하철역_목록에_생성한_역이_포함된다(지하철역_목록, "강남역");
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @Test
    void 기존에_존재하는_지하철역_이름으로_지하철역을_생성한다() {
        // given
        지하철역_생성("강남역");

        // when
        ExtractableResponse<Response> 강남역 = 지하철역_생성("강남역");

        // then
        지하철역_생성_실패를_확인한다(강남역);
    }


    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @Test
    void 지하철역을_조회한다() {
        // given
        지하철역_생성("강남역");
        지하철역_생성("잠실역");

        // when
        List<String> 지하철역_목록 = 지하철역_목록_조회();

        // then
        지하철역_개수를_확인한다(지하철역_목록, 2);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @Test
    void 지하철역을_제거한다() {
        // given
        ExtractableResponse<Response> 강남역 = 지하철역_생성("강남역");

        // when
        지하철역_제거(강남역);

        // then
        List<String> 지하철역_목록 = 지하철역_목록_조회();
        지하철역_목록에_생성한_역이_존재하지_않는다(지하철역_목록, "강남역");
    }

    public static class StationAcceptanceTemplate {
        public static ExtractableResponse<Response> 지하철역_생성(String stationName) {
            Map<String, String> params = new HashMap<>();
            params.put("name", stationName);

            return RestAssured.given().log().all()
                    .body(params)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().post("/stations")
                    .then().log().all()
                    .extract();
        }

        public static void 지하철역_생성_성공을_확인한다(ExtractableResponse<Response> response) {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        }

        public static List<String> 지하철역_목록_조회() {
            return RestAssured.given().log().all()
                    .when().get("/stations")
                    .then().log().all()
                    .extract().jsonPath()
                    .getList( "name", String.class);
        }

        public static void 지하철역_목록에_생성한_역이_포함된다(List<String> stationNames, String station) {
            assertThat(stationNames).containsAnyOf(station);
        }

        public static void 지하철역_생성_실패를_확인한다(ExtractableResponse<Response> response) {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        public static void 지하철역_목록에_생성한_역이_존재하지_않는다(List<String> stationNames, String station) {
            assertThat(stationNames).containsAnyOf(station);
        }

        public static ValidatableResponse 지하철역_제거(ExtractableResponse<Response> response) {
            return RestAssured.given().log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().delete("/stations" + response.body().jsonPath().getLong("id"))
                    .then().log().all();
        }

        public static void 지하철역_개수를_확인한다(List<String> stationNames, int size) {
            assertThat(stationNames).hasSize(size);
        }
    }
}
