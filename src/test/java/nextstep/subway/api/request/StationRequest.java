package nextstep.subway.api.request;

import java.util.HashMap;
import java.util.Map;

public class StationRequest {
    private static final String KEY_NAME = "name";
    private static final Map<String, String> params = new HashMap<>();

    public static Map<String, String> createParams(String name) {
        params.put(KEY_NAME, name);
        return params;
    }
}
