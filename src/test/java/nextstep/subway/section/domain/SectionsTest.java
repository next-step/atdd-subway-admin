package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {

    private Line line;
    private Station upStation;
    private Station downStation;
    private Section section;


    @BeforeEach
    void setUp() {
        line = new Line("2호선", "green");
        upStation = new Station("강남역");
        downStation = new Station("역삼역");
        section = new Section(line, upStation, downStation, 10);
    }

    @Test
    void addSection() {
        Sections sections = new Sections();

        sections.addSection(section);

        assertThat(sections.getSections()).containsExactly(section);
    }
}
