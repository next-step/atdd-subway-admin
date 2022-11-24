package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.utils.RestAssuredRequestBuilder.delete;
import static nextstep.subway.utils.RestAssuredRequestBuilder.postWithPathValue;

public class SectionAcceptanceTestUtil {
    private static final String REQUEST_PATH_FOR_SECTION = "/lines/{lineId}/sections";

    private SectionAcceptanceTestUtil() {

    }

    public static ExtractableResponse<Response> createSection(String upStationId, String downStationId, String distance, String lineId) {
        Map<String, String> params = getParams(upStationId, downStationId, distance);
        return postWithPathValue(params, REQUEST_PATH_FOR_SECTION, lineId, MediaType.APPLICATION_JSON_VALUE);
    }

    public static ExtractableResponse<Response> removeSection(String lineId, String stationId) {
        return delete(REQUEST_PATH_FOR_SECTION + "?stationId={stationId}", lineId, stationId, HttpStatus.OK);
    }

    private static Map<String, String> getParams(String upStationId, String downStationId, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return params;
    }
}
