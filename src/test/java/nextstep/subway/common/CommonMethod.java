package nextstep.subway.common;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class CommonMethod {
    public static ExtractableResponse<Response> 지하철역_생성_요청(String name) {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        // when
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color) {
        //given
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        // when
        // 지하철_노선_생성_요청
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, String> createParams) {
        // when
        // 지하철_노선_생성_요청
        return RestAssured
                .given().log().all()
                .body(createParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }
}
