package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.Map;

/**
 * packageName : nextstep.subway.utils
 * fileName : ResponseUtils
 * author : haedoang
 * date : 2021-11-17
 * description :
 */
public class RestAssuredUtils {

    private RestAssuredUtils() {
    }

    public static Response postResponse(Map<String, String> params, String uri) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(uri);
    }
}
