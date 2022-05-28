package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;

public class StationAssuredMethod {

    private static final String RESOURCE_STATIONS = "/stations";
    private static final String RESOURCE_STATIONS_ID = "/stations/{id}";

    public static ExtractableResponse<Response> 지하철역_생성_요청(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(RESOURCE_STATIONS)
                .then().log().all()
                .extract();
    }

    public static void 지하철역_삭체_요청(Long id) {
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(RESOURCE_STATIONS_ID, id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_조회_요청() {
        return RestAssured.given().log().all()
                .when().get(RESOURCE_STATIONS)
                .then().log().all()
                .extract();
    }
}
