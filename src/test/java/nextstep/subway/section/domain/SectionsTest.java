package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {

    private Line line;
    private Station upStation;
    private Station downStation;
    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;
    private Section section;
    Sections sections;

    @BeforeEach
    void setUp() {
        line = new Line("2호선", "green");
        upStation = new Station("강남역");
        downStation = new Station("역삼역");
        sections = new Sections();

        station1 = new Station("강남역");
        ReflectionTestUtils.setField(station1, "id", 1L);

        station2 = new Station("잠실역");
        ReflectionTestUtils.setField(station2, "id", 2L);

        station3 = new Station("선릉역");
        ReflectionTestUtils.setField(station3, "id", 3L);

        station4 = new Station("역삼역");
        ReflectionTestUtils.setField(station4, "id", 4L);

        sections.addSection(new Section(station1, station4, 5));
        sections.addSection(new Section(station4, station3, 5));
        sections.addSection(new Section(station3, station2, 3));


        section = new Section(line, upStation, downStation, 10);
    }

    @Test
    void addSection() {
        Sections sections = new Sections();

        sections.addSection(section);

        assertThat(sections.getSections()).containsExactly(section);
    }

    @Test
    void getStations() {
        assertThat(sections.getStations())
                .containsExactly(station1, station4, station3, station2);
    }

    @Test
    void getFirstSection() {
        Section firstSection = sections.findFirstSection()
                .orElseThrow(() -> new EntityNotFoundException());

        assertThat(firstSection.getStations())
                .containsExactly(station1, station4);
    }
}
