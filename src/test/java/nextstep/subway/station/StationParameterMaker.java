package nextstep.subway.station;

import java.util.HashMap;
import java.util.Map;

public final class StationParameterMaker {

    private StationParameterMaker() {
    }

    public static Map<String, String> 생성(String 역이름) {
        Map<String, String> subwayParameter = new HashMap<>();
        subwayParameter.put("name", 역이름);
        return subwayParameter;
    }

}
