package nextstep.subway.utils;

import java.util.HashMap;
import java.util.Map;

public class RequestParamUtils {

    public static Map<String, String> requestParam;

    static {
        requestParam = new HashMap<>();
    }

    public static Map<String, String> generateRequestParam(final String property, final String value) {
        if (isNotEmpty()) {
            clear();
        }
        requestParam.put(property, value);
        return requestParam;
    }

    private static boolean isNotEmpty() {
        return !requestParam.isEmpty();
    }

    private static void clear() {
        requestParam.clear();
    }
}
