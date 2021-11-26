package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionTest {

    @DisplayName("구역 생성 검증")
    @Test
    void create() {
        Station upStation = new Station("건대역");
        Station downStation = new Station("용마산역");
        Line line = new Line("bg-red-600", "7호선");

        Section section = new Section(line, upStation, downStation, 10);
        assertThat(section).isNotNull();
    }
}
