package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.exceptions.InvalidSectionException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionTest {
    @DisplayName("상행역과 하행역을 같은 역으로 생성 할 수 없다.")
    @Test
    void upStationDownStationCannotSameTest() {
        Line line = new Line("지옥의 2호선", "초로딩딩한 색");
        Long distance = 10L;

        assertThatThrownBy(() -> new Section(line, StationFixtures.ID1_STATION, StationFixtures.ID1_STATION, distance))
                .isInstanceOf(InvalidSectionException.class);
    }

    @DisplayName("거리가 0인 구간은 생성할 수 없다.")
    @Test
    void distanceZeroDenyTest() {
        Line line = new Line("지옥의 2호선", "초로딩딩한 색");
        Station gangnam = new Station("갱남");
        Station seocho = new Station("서초");
        Long distance = 0L;

        assertThatThrownBy(() -> new Section(line, gangnam, seocho, distance))
                .isInstanceOf(InvalidSectionException.class);
    }
}
