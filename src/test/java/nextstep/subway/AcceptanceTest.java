package nextstep.subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class AcceptanceTest {

    protected ExtractableResponse<Response> get(String path) {
        return RestAssured.given().log().all()
            .when().get(path)
            .then().log().all()
            .extract();
    }

    protected <T> ExtractableResponse<Response> post(String path, T requestBody) {
        return RestAssured.given().log().all()
            .body(requestBody)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post(path)
            .then().log().all()
            .extract();
    }

    protected ExtractableResponse<Response> delete(String path) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete(path)
            .then().log().all()
            .extract();
    }

}
