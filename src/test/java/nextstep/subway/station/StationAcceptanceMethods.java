package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceMethods;

import java.util.HashMap;
import java.util.Map;

public class StationAcceptanceMethods extends AcceptanceMethods {
    private static final String STATIONS_PATH = "/stations";

    private StationAcceptanceMethods() {
    }

    public static ExtractableResponse<Response> 지하철역_생성(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        return post(STATIONS_PATH, params);
    }

    public static ExtractableResponse<Response> 지하철역_목록_조회() {
        return get(STATIONS_PATH);
    }

    public static void 지하철역_삭제(long id) {
        delete(STATIONS_PATH + SLASH + id);
    }
}
