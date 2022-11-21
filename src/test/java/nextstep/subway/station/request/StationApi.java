package nextstep.subway.station.request;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class StationApi {
    public static ExtractableResponse<Response> createStation(final String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }
    public static ExtractableResponse<Response> deleteStation(Long stationId) {
        return RestAssured.given().log().all().
                when().
                delete(String.format("/stations/%s", stationId)).
                then().log().all().
                extract();
    }
    public static ExtractableResponse<Response> getStations() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract();
    }
}
