package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class StationRequest {
    private static final String PATH = "/stations";

    public static void 지하철역_존재(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(PATH)
                .then().log().all();
    }

    public static ExtractableResponse<Response> 지하철역_생성_요청(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(PATH)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_조회_요청() {
        return RestAssured.given().log().all()
                .when().get(PATH)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_삭제_요청(Long id) {
        return RestAssured.given().log().all()
                .when().delete(PATH + "/" + id)
                .then().log().all()
                .extract();
    }
}
