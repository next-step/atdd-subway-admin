package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


public class CommonMethodFixture {

    public static <T> ExtractableResponse<Response> get(String path) {
        return RestAssured.given().log().all()
                .when().get(path)
                .then().log().all()
                .extract();
    }

    public static <T> ExtractableResponse<Response> post(String path, T requestBody) {
        return RestAssured.given()
                .body(requestBody).log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(path)
                .then()
                .log().all()
                .extract();
    }


    public static <T> ExtractableResponse<Response> put(String path, T requestBody) {
        return RestAssured.given()
                .body(requestBody).log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(path)
                .then()
                .log().all()
                .extract();
    }

    public static <T> ExtractableResponse<Response> delete(String path) {
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(path)
                .then().log().all()
                .extract();
    }
}
