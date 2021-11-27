package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

public class TestRequestFactory {
    public static ExtractableResponse<Response> 요청(HttpMethod httpMethod, String path, Object requestBody) {
        RequestSpecification requestSpecification = RestAssured
                .given().log().all()
                .when();

        if (HttpMethod.GET.equals(httpMethod)) {
            return requestSpecification
                    .get(path)
                    .then().log().all().extract();
        }

        if (HttpMethod.PUT.equals(httpMethod)) {
            return requestSpecification
                    .body(requestBody)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .put(path)
                    .then().log().all().extract();
        }

        if (HttpMethod.DELETE.equals(httpMethod)) {
            return requestSpecification
                    .delete(path)
                    .then().log().all().extract();
        }

        return requestSpecification
                .body(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(path)
                .then().log().all().extract();
    }
}
