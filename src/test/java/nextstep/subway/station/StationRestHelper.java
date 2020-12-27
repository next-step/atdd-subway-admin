package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;

/**
 * @author : leesangbae
 * @project : subway
 * @since : 2020-12-24
 */
public class StationRestHelper {

    public static ExtractableResponse<Response> 지하철역_생성(String name) {
        return RestAssured.given().log().all()
                .body(stationParamsGenerator(name))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_삭제(String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_전체_조회() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }

    private static Map<String, String> stationParamsGenerator(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        return params;
    }
}
