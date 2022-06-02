package nextstep.subway.helper;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class LineRequest {
    private static final String PATH = "/lines";

    private LineRequest() {}

    public static ExtractableResponse<Response> getAllLines() {
        return RequestHelper.getRequest(PATH, new HashMap<>());
    }

    public static ExtractableResponse<Response> getLineById(Long id) {
        return RequestHelper.getRequest(PATH + "/{id}", new HashMap<>(), id);
    }

    public static ExtractableResponse<Response> createLine(
            String name, String color, Long upStationId, Long downStationId, Long distance
    ) {
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("color", color);
        body.put("upStationId", upStationId);
        body.put("downStationId", downStationId);
        body.put("distance", distance);

        return RequestHelper.postRequest(PATH, new HashMap<>(), body);
    }

    public static Long createLineThenReturnId(
            String name, String color, Long upStationId, Long downStationId, Long distance
    ) {
        return createLine(name, color, upStationId, downStationId, distance).jsonPath().getLong("id");
    }

    public static ExtractableResponse<Response> updateLine(Long id, String name, String color) {
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("color", color);

        return RequestHelper.putRequest(PATH + "/{id}", new HashMap<>(), body, id);
    }

    public static ExtractableResponse<Response> deleteLine(Long id) {
        return RequestHelper.deleteRequest(PATH + "/{id}", new HashMap<>(), id);
    }

    public static ExtractableResponse<Response> addSection(
            Long id, Long upStationId, Long downStationId, Long distance
    ) {
        Map<String, Object> body = new HashMap<>();
        body.put("upStationId", upStationId);
        body.put("downStationId", downStationId);
        body.put("distance", distance);

        return RequestHelper.postRequest(PATH + "/{id}/sections", new HashMap<>(), body, id);
    }
}
