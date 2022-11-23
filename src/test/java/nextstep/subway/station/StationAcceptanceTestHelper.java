package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public final class StationAcceptanceTestHelper {

    static Map<String, JsonPath> createStations(String... names) {
        final Map<String, JsonPath> result = new HashMap<>(names.length);
        for (String name : names) {
            result.put(name, createStation(name));
        }
        return result;
    }

    private static JsonPath createStation(final String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/stations")
            .then().log().all()
            .statusCode(HttpStatus.CREATED.value())
            .extract().jsonPath();
    }

    static JsonPath getStations() {
        return RestAssured.given().log().all()
            .when().get("/stations")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .extract().jsonPath();
    }

}
