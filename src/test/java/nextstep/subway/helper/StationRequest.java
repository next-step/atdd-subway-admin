package nextstep.subway.helper;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.*;

import static org.junit.jupiter.api.Assertions.fail;

public class StationRequest {
    private static final String PATH = "/stations";

    private StationRequest() {}

    public static ExtractableResponse<Response> getAllStations() {
        return RequestHelper.getRequest(PATH, new HashMap<>());
    }

    public static ExtractableResponse<Response> createStation(Map<String, Object> body) {
        List<String> essentialKeys = Arrays.asList("name");

        if (!body.keySet().containsAll(essentialKeys)) {
            fail("필수값이 누락되었습니다.");
        }

        return RequestHelper.postRequest(PATH, new HashMap<>(), body);
    }

    public static ExtractableResponse<Response> deleteStation(Long stationId) {
        return RequestHelper.deleteRequest(PATH + "/{id}", new HashMap<>(), stationId);
    }
}
