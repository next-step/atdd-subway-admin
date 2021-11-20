package nextstep.subway.api;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.Map;

public class HttpMethod {
    public static ExtractableResponse<Response> 지하철_노선_등록(Map<String, String> params) {
        return 등록요청(params, "/lines");
    }

    public static ExtractableResponse<Response> 등록요청(Map<String, String> params, String path) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(path)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회() {
        return RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회(ExtractableResponse response) {
        return RestAssured.given().log().all()
            .when()
            .get(response.header("Location"))
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정(ExtractableResponse response, Map<String, String> params) {

        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(params)
            .when()
            .put(response.header("Location"))
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_제거(ExtractableResponse response) {
        return RestAssured.given().log().all()
            .when()
            .delete(response.header("Location"))
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_역_등록(Map<String, String> params) {
        return 등록요청(params, "/stations");
    }

}
