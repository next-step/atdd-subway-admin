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
        Station 서초역 = new Station("서초역");
        Station 논현역 = new Station("논현역");
        Station 신도림역 = new Station("신도림역");
        Station 신림역 = new Station("신림역");
        Station 교대역 = new Station("교대역");
        Station 강남역 = new Station("강남역");
        List<Section> sections = Arrays.asList(
                new Section(line, 서초역, 강남역, 1),
                new Section(line, 신림역, 서초역, 1),
                new Section(line, 교대역, 신림역, 1),
                new Section(line, 논현역, 교대역, 1),
                new Section(line, 신도림역, 논현역, 1)
        );
        assertThat(new SectionNavigator(sections).orderedStations()).containsSequence(신도림역, 논현역, 교대역, 신림역, 서초역, 강남역);
    }
}
