package nextstep.subway.line;

import nextstep.subway.line.domain.Section;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.common.vo.DistanceTest.논현역_신논현역_거리;
import static nextstep.subway.common.vo.DistanceTest.신논현역_강남역_거리;
import static nextstep.subway.line.LineTest.신분당선;
import static nextstep.subway.station.domain.StationTest.*;
import static org.assertj.core.api.Assertions.assertThatNoException;

@DisplayName("구간")
class SectionTest {

    public static final Section 신논현역_강남역_구간 = new Section(1L, 신분당선, 신논현역, 강남역, 신논현역_강남역_거리);
    public static final Section 논현역_신논현역_구간 = new Section(1L, 신분당선, 논현역, 신논현역, 논현역_신논현역_거리);

    @DisplayName("구간 생성")
    @Test
    void constructor() {
        assertThatNoException().isThrownBy(() -> new Section(신분당선, 논현역, 신논현역, 논현역_신논현역_거리));
    }
}
