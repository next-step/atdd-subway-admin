package nextstep.subway.assured;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class RestAssuredApi {

    public static ExtractableResponse<Response> post(String uri, Object params) {
        return RestAssured.given().log().params()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> get(String uri) {
        return RestAssured.given().log().params()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> put(String uri, Object params) {
        return RestAssured.given().log().params()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().headers()
                .extract();
    }

    public static ExtractableResponse<Response> delete(String uri) {
        return RestAssured.given().log().params()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(uri)
                .then().log().headers()
                .extract();
    }
}
