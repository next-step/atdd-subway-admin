package nextstep.subway.util;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.StationRequest;
import org.springframework.http.MediaType;

public class StationAcceptanceMethods {
    public static ExtractableResponse<Response> createStation(String stationName) {
        StationRequest request = new StationRequest(stationName);

        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> deleteStation(Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/stations/" + id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> getAllStations() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/stations/")
                .then().log().all()
                .extract();
    }
}
