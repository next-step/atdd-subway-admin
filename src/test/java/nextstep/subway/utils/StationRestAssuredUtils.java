package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import javax.servlet.http.PushBuilder;
import java.util.HashMap;
import java.util.Map;

public class StationRestAssuredUtils {

    public static ExtractableResponse<Response> 지하철_역_생성_요청(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all().extract();
    }

    public static void 지하철_역_여러개_생성_요청() {
        Map<String, String> stationParams = new HashMap<>();
        stationParams.put("name", "강남역");
        지하철_역_생성_요청(stationParams);
        stationParams.put("name", "역삼역");
        지하철_역_생성_요청(stationParams);
    }

}
