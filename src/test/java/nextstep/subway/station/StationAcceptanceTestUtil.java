package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.utils.RestAssuredRequestBuilder.*;

public class StationAcceptanceTestUtil {
    private static final String REQUEST_PATH_FOR_STATION = "/stations";

    private StationAcceptanceTestUtil() {

    }

    public static ExtractableResponse<Response> createStation(String stationName) {
        Map<String, String> params = getParams(stationName);
        return post(params, REQUEST_PATH_FOR_STATION, MediaType.APPLICATION_JSON_VALUE);
    }

    public static ExtractableResponse<Response> getStations() {
        return get(REQUEST_PATH_FOR_STATION);
    }

    public static ExtractableResponse<Response> deleteStation(Long id) {
        return delete(REQUEST_PATH_FOR_STATION + "/{id}", id, HttpStatus.NO_CONTENT);
    }

    private static Map<String, String> getParams(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        return params;
    }
}
