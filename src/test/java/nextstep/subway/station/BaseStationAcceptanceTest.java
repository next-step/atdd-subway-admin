package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptacneTest;
import nextstep.subway.dto.StationRequest;
import org.springframework.http.MediaType;

public abstract class BaseStationAcceptanceTest extends BaseAcceptacneTest {
    private static final String STATIONS_URI = "/stations";

    public static ExtractableResponse<Response> createStationRequest(String stationName) {
        return RestAssured.given().log().all()
                .body(StationRequest.from(stationName))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(STATIONS_URI)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> findStationsRequest() {
        return RestAssured.given().log().all()
                .when().get(STATIONS_URI)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> deleteStationRequest(int stationId) {
        return RestAssured.given().log().all()
                .pathParam("id", stationId)
                .when().delete(STATIONS_URI + "/{id}")
                .then().log().all()
                .extract();
    }
}
