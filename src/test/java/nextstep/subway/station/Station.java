package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.StationRequest;
import org.springframework.http.MediaType;

public class Station {
    private Station() {
    }

    public static ExtractableResponse<Response> createStation(String name) {
        StationRequest request = createStationRequest(name);
        return createStation(request);
    }

    public static StationRequest createStationRequest(String name) {
        return new StationRequest(name);
    }

    public static ExtractableResponse<Response> createStation(StationRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }
}
