package nextstep.subway.line.accecptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class LineAcceptanceRequest {
    public static ExtractableResponse<Response> 지하철노선_생성_요청() {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("upStationId", 1L);
        params.put("downStationId", 2L);
        params.put("distance", 10);
        params.put("color", "green");

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    public static void 지하철노선_존재(String name, Long upStationId, Long downStationId, int distance, String color) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        params.put("color", color);

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철노선_목록_조회_요청() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철노선_조회_요청() {
        return RestAssured.given().log().all()
                .when().get("/lines/1")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철노선_수정_요청_성공(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().patch("/lines/1")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철노선_삭제_요청_성공(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().delete("/lines/1")
                .then().log().all()
                .extract();
    }
}