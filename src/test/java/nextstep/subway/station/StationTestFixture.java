package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class StationTestFixture {

    public static ValidatableResponse create(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all();
    }

    public static ValidatableResponse delete(long id) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .when().delete("/stations/{id}")
                .then().log().all();
    }

    public static ValidatableResponse fetch() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all();
    }
}
