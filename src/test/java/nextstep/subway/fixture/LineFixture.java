package nextstep.subway.fixture;

import nextstep.subway.line.domain.Line;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static nextstep.subway.fixture.StationFixture.*;

public class LineFixture {
    public static Line 팔호선 = Line.of("팔호선", "pink");
    public static Line 이호선 = Line.of("이호선", "green");

    public static List<Map<String, String>> 팔호선_역 = Arrays.asList(암사역, 천호역, 강동구청역, 몽촌토성역, 잠실역);

    public static List<Integer> 팔호선_구간_거리 = Arrays.asList(1300, 900, 1600, 800);
}
