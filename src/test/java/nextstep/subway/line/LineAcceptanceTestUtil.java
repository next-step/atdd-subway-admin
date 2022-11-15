package nextstep.subway.line;


import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.utils.RestAssuredRequestBuilder.*;

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

    public static ExtractableResponse<Response> updateLine(String newName, String newColor, Long id) {
        Map<String, String> params = new HashMap<>();
        params.put("name", newName);
        params.put("color", newColor);
        return put(params, MediaType.APPLICATION_JSON_VALUE, REQUEST_PATH_FOR_LINE + "/{id}", id, HttpStatus.OK);
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
