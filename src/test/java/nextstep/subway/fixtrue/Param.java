package nextstep.subway.fixtrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Param {
    private Map<String, Object> paramMap = new HashMap<>();

    public static Param createParam() {
        return new Param();
    }

    public Param addParam(String key, String value) {
        paramMap.put(key, value);
        return this;
    }

    public Param addParam(String key, Long value) {
        paramMap.put(key, value);
        return this;
    }

    public Map<String,Object> result() {
        return Collections.unmodifiableMap(paramMap);
    }

}
