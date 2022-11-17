package nextstep.subway.line;

import org.junit.jupiter.api.DisplayName;

import static nextstep.subway.common.vo.DistanceTest.논현역_신논현역_거리;
import static nextstep.subway.common.vo.DistanceTest.신논현역_강남역_거리;
import static nextstep.subway.line.LineTest.신분당선;
import static nextstep.subway.station.domain.StationTest.*;

@DisplayName("구간")
class SectionTest {

    public static final Section 신논현역_강남역_구간 = new Section(1L, 신분당선, 신논현역, 강남역, 신논현역_강남역_거리);
    public static final Section 논현역_신논현역_구간 = new Section(1L, 신분당선, 논현역, 신논현역, 논현역_신논현역_거리);

}
