package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

import static nextstep.subway.station.StationTestUtils.*;

public class LineTestUtils {
    public static final String PATH = "/lines";

    public static ExtractableResponse<Response> 지하철노선_생성_요청(String lineName, String lineColor, String upStationName, String downStationName, int distance) {
        Long upStationId = 지하철역_생성_요청(upStationName).jsonPath().getLong("id");
        Long downStationId = 지하철역_생성_요청(downStationName).jsonPath().getLong("id");

        LineRequest request = new LineRequest(lineName, lineColor, upStationId, downStationId, distance);

        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(PATH)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철노선_생성_요청(String lineName, String lineColor, Long upStationId, Long downStationId, int distance) {
        LineRequest request = new LineRequest(lineName, lineColor, upStationId, downStationId, distance);

        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(PATH)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철노선_목록_조회() {
        return RestAssured.given().log().all()
                .when().get(PATH)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철노선_조회(Long lineId) {
        return RestAssured.given().log().all()
                .pathParam("id", lineId)
                .when().get(PATH + "/{id}")
                .then().log().all()
                .extract();
    }

    public static List<String> 지하철노선_역목록_조회(Long lineId) {
        return 지하철노선_조회(lineId).jsonPath().getList("stations.name", String.class);
    }

    public static ExtractableResponse<Response> 지하철노선_수정_요청(Long lineId, String lineName, String lineColor) {
        Map<String, String> params = new HashMap<>();
        params.put("name", lineName);
        params.put("color", lineColor);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", lineId)
                .when().put(PATH + "/{id}")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철노선_삭제_요청(Long lineId) {
        return RestAssured.given().log().all()
                .pathParam("id", lineId)
                .when().delete(PATH + "/{id}")
                .then().log().all()
                .extract();
    }

    public static void 지하철노선_생성_성공_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 지하철노선_생성_실패_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철노선_조회_성공_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철노선_수정_성공_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철노선_삭제_성공_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static Long 지하철노선_ID_조회(ExtractableResponse<Response> response) {
        return response.jsonPath().getLong("id");
    }

    public static void 지하철노선_포함_확인(String lineName) {
        List<String> lineNames = 지하철노선_목록_조회().jsonPath().getList("name", String.class);
        assertThat(lineNames).containsAnyOf(lineName);
    }

    public static void 지하철노선_미포함_확인(String lineName) {
        List<String> lineNames = 지하철노선_목록_조회().jsonPath().getList("name", String.class);
        assertThat(lineNames).doesNotContain(lineName);
    }

    public static void 지하철노선_이름_확인(ExtractableResponse<Response> response, String lineName) {
        String name = response.jsonPath().getString("name");
        assertThat(name).isEqualTo(lineName);
    }

    public static void 지하철노선_색_확인(ExtractableResponse<Response> response, String lineColor) {
        String color = response.jsonPath().getString("color");
        assertThat(color).isEqualTo(lineColor);
    }

    public static void 지하철노선_역갯수_확인(Long lineId, int size) {
        List<String> stationNames = 지하철노선_역목록_조회(lineId);
        assertThat(stationNames).hasSize(size);
    }

    public static void 지하철노선_역순서_확인(Long lineId, List<String> orderedStationNames) {
        List<String> stationNames = 지하철노선_역목록_조회(lineId);
        assertThat(stationNames).isEqualTo(orderedStationNames);
    }
}
