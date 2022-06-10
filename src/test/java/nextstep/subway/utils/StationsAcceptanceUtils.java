package nextstep.subway.utils;

import static nextstep.subway.station.StationAcceptanceTest.STATION_BASE_URL;

import io.restassured.response.Response;
import nextstep.subway.dto.StationRequest;

public class StationsAcceptanceUtils {

    public static Response generateStation(final String stationName) {
        StationRequest stationRequest = new StationRequest(stationName);
        return RestAssuredUtils.post(STATION_BASE_URL, stationRequest).extract().response();
    }
}
