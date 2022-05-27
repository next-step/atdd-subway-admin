package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;

public class AttdStationUtils {
    public static ExtractableResponse<Response> 지하철역_지우기(long 지하철역_id) {
        return RestAssured.given().log().all()
            .body("")
            .when().delete("/stations/" + 지하철역_id)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철목록_조회하기() {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/stations")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철상세_조회하기(long 지하철_ID) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/stations/" + 지하철_ID)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철역_수정하기(String 지하철_ID, String 생성할_지하철역_이름) {
        Map<String, String> params = new HashMap<>();
        params.put("name", 생성할_지하철역_이름);
        params.put("id",지하철_ID);
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/stations")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철역_만들기(String 생성할_지하철역_이름) {
        Map<String, String> params = new HashMap<>();
        params.put("name", 생성할_지하철역_이름);
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/stations")
            .then().log().all()
            .extract();
    }
}
