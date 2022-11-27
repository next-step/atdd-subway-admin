package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import nextstep.subway.station.StationResponse;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class LineTestFixture {

    public static ValidatableResponse create(String name, String color, StationResponse upStation, StationResponse downStation, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStation.getId());
        params.put("downStationId", downStation.getId());
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all();
    }

    public static ValidatableResponse createSection(long lineId, StationResponse upStation, StationResponse downStation, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStation.getId());
        params.put("downStationId", downStation.getId());
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", lineId)
                .when().post("/lines/{id}/sections")
                .then().log().all();
    }

    public static ValidatableResponse fetchAll() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all();
    }

    public static ValidatableResponse fetch(long lineId) {
        return RestAssured.given().log().all()
                .pathParam("id", lineId)
                .when().get("/lines/{id}")
                .then().log().all();
    }

    public static ValidatableResponse update(long lineId, String name, String color) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured.given().log().all()
                .body(params)
                .pathParam("id", lineId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{id}")
                .then().log().all();
    }

    public static ValidatableResponse delete(long lineId) {
        return RestAssured.given().log().all()
                .pathParam("id", lineId)
                .when().delete("/lines/{id}")
                .then().log().all();
    }
}
