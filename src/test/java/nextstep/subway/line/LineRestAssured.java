package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class LineRestAssured {
    private static final String RESOURCE = "/lines";
    
    public static ExtractableResponse<Response> 노선_등록(String name, String color, Long upStationId, Long downStationId, Integer distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(RESOURCE)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선_목록_조회() {
        return RestAssured.given().log().all()
                .when().get(RESOURCE)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선_조회(Long lineId) {
        return RestAssured.given().log().all()
                .when().get(RESOURCE + "/{id}", lineId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선_수정(Long lineId, String name, String color) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().put(RESOURCE + "/{id}", lineId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선_삭제(Long lineId) {
        return RestAssured.given().log().all()
                .when().delete(RESOURCE + "/{id}", lineId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선_구간_추가(Long lineId, Long upStationId, Long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(RESOURCE + "/{id}/sections", lineId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선_구간_삭제(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .queryParam("stationId", stationId)
                .when().delete(RESOURCE + "/{id}/sections", lineId)
                .then().log().all()
                .extract();
    }
}
