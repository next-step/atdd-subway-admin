package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class LineRestAssured {
    private static final String RESOURCE = "/lines";

    public static ExtractableResponse<Response> 노선_등록(String name, String color, Long upStationId, Long downStationId) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(RESOURCE)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선_조회() {
        return RestAssured.given().log().all()
                .when().get(RESOURCE)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선_삭제(Long stationId) {
        return RestAssured.given().log().all()
                .accept(ContentType.JSON)
                .when().delete(RESOURCE + "/{id}", stationId)
                .then().log().all()
                .extract();
    }
}
