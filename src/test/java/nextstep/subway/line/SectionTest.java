package nextstep.subway.line;

import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.StationFixtrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.line.LineFixture.신분당선;
import static org.assertj.core.api.Assertions.assertThatNoException;

@DisplayName("구간")
public
class SectionTest {

    public static final int 신논현역_강남역_거리 = 5;
    public static final int 논현역_신논현역_거리 = 4;
    public static final int 강남역_역삼역_거리 = 4;
    public static final int 강남역_선릉역_거리 = 6;
    public static final int 논현역_강남역_거리 = 4;
    public static final int 역삼역_선릉역_거리 = 2;

    @DisplayName("구간 생성")
    @Test
    void constructor() {
        assertThatNoException().isThrownBy(() -> new Section(신분당선(), StationFixtrue.논현역(), StationFixtrue.신논현역(), new Distance(논현역_신논현역_거리)));
    }
}
