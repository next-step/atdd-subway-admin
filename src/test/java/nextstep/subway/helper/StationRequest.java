package nextstep.subway.helper;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class StationRequest {
    private static final String PATH = "/stations";

    private StationRequest() {}

    public static ExtractableResponse<Response> getAllStations() {
        return RequestHelper.getRequest(PATH, new HashMap<>());
    }

    public static ExtractableResponse<Response> createStation(String name) {
        Map<String, String> body = new HashMap<>();
        body.put("name", name);

        return RequestHelper.postRequest(PATH, new HashMap<>(), body);
    }

    public static Long createStationThenReturnId(String name) {
        return createStation(name).jsonPath().getLong("id");
    }

    public static ExtractableResponse<Response> deleteStation(Long stationId) {
        return RequestHelper.deleteRequest(PATH + "/{id}", new HashMap<>(), stationId);
    }
}
