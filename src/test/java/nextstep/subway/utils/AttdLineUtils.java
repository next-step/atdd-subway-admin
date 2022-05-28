package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;

public class AttdLineUtils {

    public static ExtractableResponse<Response> 지하철_노선_등록하기(
        String 노선_이름, String 색깔, String 상향종착점_ID, String 하향종착점_ID, String 거리
    ) {
        Map<String, String> params = new HashMap<>();
        params.put("name", 노선_이름);
        params.put("color", 색깔);
        params.put("upStationId", 상향종착점_ID); //상향종착점
        params.put("downStationId", 하향종착점_ID); //상향종착점
        params.put("distance", 거리);

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all()
            .extract();
    }

}
