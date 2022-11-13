package nextstep.subway.line;

import static nextstep.subway.station.StationAcceptanceTestFixture.*;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class LineAcceptanceTestFixture {
    public static ExtractableResponse<Response> 지하철_노선_생성_강남_잠실(String name) {
        Integer upStationId = 지하철역_생성("강남역").body().jsonPath().get("id");
        Integer downStationId = 지하철역_생성("잠실역").body().jsonPath().get("id");

        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", "bg-green-600");
        params.put("upStationId", upStationId.toString());
        params.put("downStationId", downStationId.toString());
        params.put("distance", "5");

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_왕십리_죽전(String name) {
        Integer upStationId = 지하철역_생성("왕십리").body().jsonPath().get("id");
        Integer downStationId = 지하철역_생성("죽전").body().jsonPath().get("id");

        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", "bg-yellow-600");
        params.put("upStationId", upStationId.toString());
        params.put("downStationId", downStationId.toString());
        params.put("distance", "10");

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회() {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/lines")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회(Long id) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/lines/" + id)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정(Long id, String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put("/lines/" + id)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_삭제(Long id) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete("/lines/" + id)
            .then().log().all()
            .extract();
    }
}
