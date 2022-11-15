package nextstep.subway.line;


import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.utils.RestAssuredRequestBuilder.get;
import static nextstep.subway.utils.RestAssuredRequestBuilder.post;

public class LineAcceptanceTestUtil {
    private static final String REQUEST_PATH_FOR_LINE = "/lines";

    private LineAcceptanceTestUtil() {

    }

    public static ExtractableResponse<Response> createLine(String name, String color, String upStationId, String downStationId, String distance) {
        Map<String, String> params = getParams(name, color, upStationId, downStationId, distance);
        return post(params, REQUEST_PATH_FOR_LINE, MediaType.APPLICATION_JSON_VALUE);
    }

    public static ExtractableResponse<Response> getLines() {
        return get(REQUEST_PATH_FOR_LINE);
    }

    public static ExtractableResponse<Response> getLines(Long id) {
        return get(REQUEST_PATH_FOR_LINE + "/{id}", id);
    }

    private static Map<String, String> getParams(String name, String color, String upStationId, String downStationId, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return params;
    }
}
