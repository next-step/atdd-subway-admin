package nextstep.subway.station;

import io.restassured.RestAssured;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class StationHelper {
    public static void createStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }
}



