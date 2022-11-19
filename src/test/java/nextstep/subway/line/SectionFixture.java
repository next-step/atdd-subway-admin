package nextstep.subway.line;

import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Section;

import static nextstep.subway.line.LineFixture.신분당선;
import static nextstep.subway.line.SectionTest.논현역_신논현역_거리;
import static nextstep.subway.line.SectionTest.신논현역_강남역_거리;
import static nextstep.subway.station.domain.StationFixtrue.*;

public class SectionFixture {

    public static Section 신논현역_강남역_구간() {
        return new Section(1L, 신분당선(), 신논현역(), 강남역(), new Distance(신논현역_강남역_거리));
    }

    public static Section 논현역_신논현역_구간() {
        return new Section(1L, 신분당선(), 논현역(), 신논현역(), new Distance(논현역_신논현역_거리));
    }
}
