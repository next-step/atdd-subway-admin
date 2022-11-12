package nextstep.subway.line;

import static nextstep.subway.util.FixtureUtil.getIdFromLocation;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class LineFixture {

    public static ExtractableResponse<Response> 지하철_노선_생성(final String name, final String color,
        final int distance, final Long upStationId, final Long downStationId) {
        return 지하철_노선_생성(
            createParams(name, color, distance, upStationId, downStationId));
    }

    public static Long 지하철_노선_생성후_아이디_반환(final String name, final String color,
        final int distance, final Long upStationId, final Long downStationId) {
        ExtractableResponse<Response> response = 지하철_노선_생성(
            createParams(name, color, distance, upStationId, downStationId));
        return getIdFromLocation(response.header(HttpHeaders.LOCATION));
    }

    private static Map<String, String> createParams(String name, String color, int distance,
        Long upStationId, Long downStationId) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("distance", String.valueOf(distance));
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        return params;
    }

    public static ExtractableResponse<Response> 지하철_노선_생성(Map<String, String> params) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all()
            .statusCode(HttpStatus.CREATED.value())
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회() {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/lines")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회(final Long lineId) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/lines/" + lineId)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정(final Long lineId, final String name,
        final String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put("/lines/" + lineId)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_삭제(final Long lineId) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete("/lines/" + lineId)
            .then().log().all()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .extract();
    }

}
