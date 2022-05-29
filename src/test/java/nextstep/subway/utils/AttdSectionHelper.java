package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;

public class AttdSectionHelper {

    public static ExtractableResponse<Response> 지하철_구간_등록하기(String 하향_지하철역_ID, String 상향_지하철역_ID,
        String 거리, String 노선_ID) {
        Map<String, String> params = new HashMap<>();
        params.put("downStationId", 하향_지하철역_ID);
        params.put("upStationId", 상향_지하철역_ID);
        params.put("distance", 거리);

        String URI = stringAppender(Arrays.asList("/lines/", 노선_ID, "sections"));

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post(URI)
            .then().log().all()
            .extract();
    }

    private static String stringAppender(List<String> strings) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : strings) {
            stringBuilder.append(string);
        }
        return stringBuilder.toString();
    }
}
