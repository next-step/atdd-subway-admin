package nextstep.subway.line;

import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.StationFixtrue;

import static nextstep.subway.line.LineFixture.*;
import static nextstep.subway.line.LineFixture.신분당선;
import static nextstep.subway.line.SectionTest.*;
import static nextstep.subway.station.domain.StationFixtrue.*;

public class SectionFixture {

    public static Section 신논현역_강남역_구간() {
        return new Section(1L, 신분당선(), 신논현역(), 강남역(), new Distance(신논현역_강남역_거리));
    }

    public static Section 논현역_신논현역_구간() {
        return new Section(2L, 신분당선(), 논현역(), 신논현역(), new Distance(논현역_신논현역_거리));
    }

    public static Section 강남역_역삼역_구간() {
        return new Section(3L, 이호선(), StationFixtrue.강남역(), 역삼역(), new Distance(강남역_역삼역_거리));
    }

    public static Section 강남역_선릉역_구간() {
        return new Section(4L, 이호선(), StationFixtrue.강남역(), 선릉역(), new Distance(강남역_선릉역_거리));
    }
    
    public static Section 논현역_강남역_구간() {
        return new Section(5L, 이호선(), 논현역(), 강남역(), new Distance(논현역_강남역_거리));
    }

    public static Section 논현역_강남역_구간_짧은거리() {
        return new Section(6L, 이호선(), 논현역(), 강남역(), new Distance(2));
    }
    
    public static Section 역삼역_선릉역_구간() {
        return new Section(7L, 이호선(), 역삼역(), 선릉역(), new Distance(역삼역_선릉역_거리));
    }

    public static Section sectionAB() {
        return new Section(8L, lineA(), stationA(), stationB(), new Distance(DISTANCE_A_B));
    }

    public static Section sectionAC() {
        return new Section(9L, lineA(), stationA(), stationC(), new Distance(DISTANCE_A_C));
    }

    public static Section sectionBC() {
        return new Section(10L, lineA(), stationB(), stationC(), new Distance(DISTANCE_B_C));
    }
}
