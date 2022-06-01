package nextstep.subway.line;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionNavigator;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectionNavigatorTest {
    @DisplayName("구간들을 순서대로 탐색하여 역을 조회")
    @Test
    void orderedStations() {
        Line line = new Line("2호선", "green");
        Station station1 = new Station("서초역");
        Station station2 = new Station("논현역");
        Station station3 = new Station("신도림역");
        Station station4 = new Station("신림역");
        Station station5 = new Station("교대역");
        Station station6 = new Station("강남역");
        List<Section> sections = Arrays.asList(
                new Section(line, station1, station6, 1),
                new Section(line, station4, station1, 1),
                new Section(line, station5, station4, 1),
                new Section(line, station2, station5, 1),
                new Section(line, station3, station2, 1)
        );
        assertThat(new SectionNavigator(sections).orderedStations()).containsSequence(station3, station2, station5, station4, station1, station6);
    }
}
