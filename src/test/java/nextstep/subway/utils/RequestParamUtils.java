package nextstep.subway.utils;

import java.util.HashMap;
import java.util.Map;

public class RequestParamUtils {

    public static Map<String, String> generateRequestParam(final String property, final String value) {
        Map<String, String> requestParam = new HashMap<>();
        requestParam.put(property, value);
        return requestParam;
    }
}
