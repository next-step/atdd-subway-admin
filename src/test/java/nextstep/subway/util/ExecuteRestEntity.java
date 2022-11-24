package nextstep.subway.util;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
public class ExecuteRestEntity {

    public ValidatableResponse select(String location) {
        return RestAssured.given().log().all()
                .when().get(location)
                .then().log().all();
    }

    public ValidatableResponse insert(Object body, String path) {
        return RestAssured.given().log().all()
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(path)
                .then().log().all();
    }

    public ValidatableResponse delete(String location) {
        return RestAssured.given().log().all()
                .when().delete(location)
                .then().log().all();
    }
}
