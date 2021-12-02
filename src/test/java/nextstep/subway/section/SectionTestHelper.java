package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class SectionTestHelper {

    public static Map 구간_생성_요청_파라미터(String upStationId, String downStationId, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return params;
    }

    public static ExtractableResponse<Response> 구간_등록_요청(Map params){
        return RestAssured.given().log().all()
                .pathParam("lineId", "1")
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{lineId}/sections")
                .then().log().all()
                .extract();
    }
}
