package nextstep.subway.line;

import java.util.HashMap;
import java.util.Map;

public class LineMap {

    private LineMap() {
    }

    public static Map of(String color, String name) {
        Map<String, String> params = new HashMap<>();
        params.put("color", color);
        params.put("name", name);
        params.put("name", name);
        params.put("upStationId", "1");
        params.put("downStationId", "2");
        params.put("distance", "10");

        return params;
    }
}
