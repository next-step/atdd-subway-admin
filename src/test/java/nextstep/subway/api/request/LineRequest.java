package nextstep.subway.api.request;

import java.util.HashMap;
import java.util.Map;

public class LineRequest {
    private static final String KEY_NAME = "name";
    private static final String KEY_COLOR = "color";
    private static final Map<String, String> params = new HashMap<>();

    public static Map<String, String> createParams(
            String name, String color, String upStationId, String downStationId) {
        params.put(KEY_NAME, name);
        params.put(KEY_COLOR, color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", "10");
        return params;
    }

    public static Map<String, String> updateParams(String name, String color) {
        params.put(KEY_NAME, name);
        params.put(KEY_COLOR, color);
        return params;
    }
}
