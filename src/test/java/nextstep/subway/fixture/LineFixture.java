package nextstep.subway.fixture;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.fixture.StationFixture.*;

public class LineFixture {
    public static final Map<String, String> 팔호선 = new HashMap<String, String>() {{
        put("color", "pink");
        put("name", "팔호선");
    }};

    public static final Map<String, String> 이호선 = new HashMap<String, String>() {{
        put("color", "green");
        put("name", "이호선");
    }};

    public static List<Map<String, String>> eightLineStations = Arrays.asList(암사역, 천호역, 강동구청역, 몽촌토성역, 잠실역, 석촌역, 송파역
        , 가락시장역, 문정역, 장지역, 복정역, 산성역, 남한산성입구역, 단대오거리역, 신흥역, 수진역, 모란역);

    public static List<Integer> eightLineDistances = Arrays.asList(1300, 900, 1600, 800, 1200, 900, 800, 900, 900, 900, 2700, 1300, 800, 800, 900, 900);

    public static List<Map<String, String>> twoLineStations = Arrays.asList(강남역, 역삼역);

    public static List<Integer> twoLineDistances = Arrays.asList(800);
}
