package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.SectionRequest;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static nextstep.subway.line.LineAcceptanceStep.노선_한개_생성한다;
import static nextstep.subway.line.LineAcceptanceStep.특정_노선을_조회한다;
import static nextstep.subway.station.StationAcceptanceTest.지하철역을_생성한다;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class LineSectionStep {

    public static ExtractableResponse<Response> 역_3개와_노선을_생성한다() {
        int upLastStationId = 지하철역을_생성한다("강남역").jsonPath().get("id");
        int downLastStationId = 지하철역을_생성한다("선릉역").jsonPath().get("id");
        지하철역을_생성한다("역삼역");

        return 노선_한개_생성한다(upLastStationId, downLastStationId);
    }

    public static ExtractableResponse<Response> 구간_생성_요청(int lineId, Long upStationId, Long downStationId, int distance) {
        SectionRequest request = SectionRequest.builder()
                .upStationId(upStationId)
                .downStationId(downStationId)
                .distance(distance)
                .build();
        return 구간_생성_호출(lineId, request);
    }


    public static ExtractableResponse<Response> 구간_생성_호출(int lineId, SectionRequest request) {
        return RestAssured.given()
                .body(request).log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/" + lineId + "/sections")
                .then()
                .log().all()
                .extract();
    }

    public static void 추가_역을_3개_생성한다() {
        지하철역을_생성한다("판교역");
        지하철역을_생성한다("건대역");
        지하철역을_생성한다("교대역");
    }


    public static void 구간_추가_등록_결과_확인(ExtractableResponse<Response> response, int count, int totalDistance) {
        // 특정_노선을_조회한다(lineId); 로 로그 확인 가능

        구간_등록_결과_검증(response, totalDistance, count);
        구간_등록_성공_확인(response);
    }
    public static void 구간_등록_성공_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 구간_등록_실패(ExtractableResponse<Response> response) {
        // 500
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private static void 구간_등록_결과_검증(ExtractableResponse<Response> response, int totalDistance, int count) {
        // List<JSONObject> stations = jsonPath.getList("stations"); 가능
        JsonPath jsonPath = response.body().jsonPath();

        assertAll(
                () -> assertThat(jsonPath.getInt("totalDistance")).isEqualTo(totalDistance),
                () -> assertThat(jsonPath.getList("stations")).hasSize(count)
        );
    }


    public static void 구간_등록_결과_검증(int lineId, int count, String target) {
        List<JSONObject> jsonObjects = 특정_노선을_조회한다(lineId).jsonPath().getList(target);
        assertThat(jsonObjects).hasSize(count);
    }


}
