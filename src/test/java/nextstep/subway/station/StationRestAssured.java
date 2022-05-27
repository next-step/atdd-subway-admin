package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class StationRestAssured {
    private static final String RESOURCE = "/stations";

    public static ExtractableResponse<Response> 지하철역_등록(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(RESOURCE)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_조회() {
        return RestAssured.given().log().all()
                .when().get(RESOURCE)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_삭제(Long stationId) {
        return RestAssured.given().log().all()
                .accept(ContentType.JSON)
                .when().delete(RESOURCE + "/{id}", stationId)
                .then().log().all()
                .extract();
    }
}