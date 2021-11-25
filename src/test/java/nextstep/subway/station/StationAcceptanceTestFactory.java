package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.RestAssuredApiTest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StationAcceptanceTestFactory {

    private static final String STATION_BASE_API_URL = "/stations";
    private static final String STATION_DELETE_API_URL = STATION_BASE_API_URL + "/{id}";

    public static ExtractableResponse<Response> 지하철_역_등록되어_있음(String name) {
        final Map<String, String> params = getStationCreateParam(name);
        return 지하철_역_생성_요청(params);
    }

    public static ExtractableResponse<Response> 지하철_역_생성_요청(Map<String, String> params) {
        return RestAssuredApiTest.post(STATION_BASE_API_URL, params);
    }

    public static ExtractableResponse<Response> 지하철_역_목록_조회_요청() {
        return RestAssuredApiTest.get(STATION_BASE_API_URL);
    }

    public static ExtractableResponse<Response> 지하철_역_제거_요청(Long id) {
        return RestAssuredApiTest.delete(STATION_DELETE_API_URL, id);
    }

    public static Map<String, String> getStationCreateParam(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        return Collections.unmodifiableMap(params);
    }
}
