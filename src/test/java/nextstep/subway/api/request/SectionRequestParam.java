package nextstep.subway.api.request;

import java.util.HashMap;
import java.util.Map;

public class SectionRequestParam {
    private static final Map<String, String> params = new HashMap<>();

    public static Map<String, String> create(String upStationId, String downStationId, String distance) {
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return params;
    }
}
