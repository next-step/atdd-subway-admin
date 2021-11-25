package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.MediaType;

public class LineAcceptanceTestUtil {

    private LineAcceptanceTestUtil() {
    }

    public static ExtractableResponse<Response> 지하철_노선_등록되어_있음(String lineName, String color,
        Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = 지하철_노선_생성_파라미터_맵핑(lineName, color, upStationId, downStationId,
            distance);
        return 지하철_노선_생성_요청(params);
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(
        ExtractableResponse<Response> createResponse) {
        long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        return RestAssured.given().log().all()
            .when()
            .get("/lines/" + lineId)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(Long lineId) {
        return RestAssured.given().log().all()
            .when()
            .get("/lines/" + lineId)
            .then().log().all()
            .extract();
    }


    public static ExtractableResponse<Response> 지하철_노선_생성_요청(
        Map<String, String> params) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("lines")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(Map<String, String> updateParams,
        ExtractableResponse<Response> createResponse) {
        long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        return RestAssured.given().log().all()
            .body(updateParams)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .patch("/lines/" + lineId)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(
        ExtractableResponse<Response> response) {
        String uri = response.header("Location");
        return RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then()
            .log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선구간_추가_요청(Long lineId,
        Map<String, String> params) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/" + lineId + "/sections")
            .then().log().all()
            .extract();
    }


    public static ExtractableResponse<Response> 지하철_노선구간_추가_되어_있음(Long lineId, Long stationId,
        Long nextStationId, int distance) {
        Map<String, String> params = 지하철_노선_구간_추가_파라미터_맵핑(stationId, nextStationId, distance);
        return 지하철_노선구간_추가_요청(lineId, params);
    }

    public static Map<String, String> 지하철_노선_생성_파라미터_맵핑(String lineName, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", lineName);
        params.put("color", color);
        return params;
    }

    public static Map<String, String> 지하철_노선_생성_파라미터_맵핑(String lineName, String color,
        Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", lineName);
        params.put("color", color);
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        params.put("distance", String.valueOf(distance));
        return params;
    }

    public static Map<String, String> 지하철_노선_구간_추가_파라미터_맵핑(Long stationId, Long nextStationId,
        int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(stationId));
        params.put("downStationId", String.valueOf(nextStationId));
        params.put("distance", String.valueOf(distance));
        return params;
    }

    public static List<Long> ids_추출_ByLineResponse(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class).stream()
            .map(it -> it.getId())
            .collect(Collectors.toList());
    }

    public static List<Long> ids_추출_ByLocation(
        List<ExtractableResponse<Response>> createResponses) {
        return createResponses.stream()
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
    }

}
