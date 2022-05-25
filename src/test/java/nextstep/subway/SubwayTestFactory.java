package nextstep.subway;

import io.restassured.RestAssured;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;

public class SubwayTestFactory {
    private static final Map<String, Object> params = new HashMap<>();

    public static void generateStation(String name) {
        params.put("name", name);

        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all();
    }

    public static ExtractableResponse<Response> generateStationToResponse(String name) {
        params.put("name", name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    public static <T> List<T> findStations(String key, Class<T> genericType) {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract().jsonPath().getList(key, genericType);
    }

    public static void deleteStationById(Long id) {
        RestAssured.given().pathParam("id", id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .log().all()
                .when().delete("/stations/{id}")
                .then().log().all();
    }
}
