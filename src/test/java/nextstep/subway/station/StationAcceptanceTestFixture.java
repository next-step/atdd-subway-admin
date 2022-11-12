package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;

public class StationAcceptanceTestFixture {

    public static final String STATION_API_MAIN_PATH = "/stations";

    public static ExtractableResponse<Response> createStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(STATION_API_MAIN_PATH)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> findAllStations() {
        return RestAssured.given().log().all()
                .when().get(STATION_API_MAIN_PATH)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> removeStation(int stationId) {
        return RestAssured.given().log().all()
                .when().delete(String.format("/stations/%d", stationId))
                .then().log().all()
                .extract();
    }
}
