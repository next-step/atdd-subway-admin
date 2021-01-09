package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class SectionRestAssuredUtils {

    public static Map<String, String> 구간_등록_요청_파라미터_생성(String upStationId, String downStationId, String distance) {
        Map<String,String> param = new HashMap<>();
        param.put("upStationId", upStationId);
        param.put("downStationId", downStationId);
        param.put("distance", distance);
        return param;
    }

    public static ExtractableResponse<Response> 구간_등록_요청(Long lineId, Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/" + lineId + "/sections")
                .then().log().all().extract();
    }
}
