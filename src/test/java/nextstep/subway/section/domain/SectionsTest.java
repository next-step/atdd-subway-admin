package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionsTest {

    @DisplayName("Sections 일급 콜렉션 객체 생성")
    @Test
    void createSections() {
        Station upStation = new Station("건대역");
        Station downStation = new Station("용마산역");
        Line line = new Line("bg-red-600", "7호선");
        Section section = new Section(line, upStation, downStation, 10);
        upStation = new Station("청담역");
        downStation = new Station("뚝섬유원지역");
        Section section2 = new Section(line, upStation, downStation, 3);

        Sections sections = Sections.empty();
        sections.add(section);
        sections.add(section2);

        assertThat(sections).isNotNull();
    }
}
