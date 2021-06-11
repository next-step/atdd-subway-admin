package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {

    private Line line;
    private Station upStation;
    private Station downStation;
    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;
    private Station station5;
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

        station5 = new Station("종합운동장");
        ReflectionTestUtils.setField(station5, "id", 5L);
    }

    @Test
    void addSection() {
        Sections sections = new Sections();

        sections.addSection(new Section(station1, station2, 20));
        sections.addSection(new Section(station1, station4, 10));
        sections.addSection(new Section(station3, station2, 2));
        sections.addSection(new Section(station3, station5, 1));

        assertThat(sections.getStations()).containsExactly(station1, station4, station3, station5, station2);
    }

    @Test
    void addFirst() {
        Sections sections = new Sections();

        sections.addSection(new Section(station3, station2, 20));
        sections.addSection(new Section(station1, station3, 10));

        assertThat(sections.getStations()).containsExactly(station1, station3, station2);
    }

    @Test
    void addLast() {
        Sections sections = new Sections();

        sections.addSection(new Section(station1, station3, 20));
        sections.addSection(new Section(station3, station2, 10));

        assertThat(sections.getStations()).containsExactly(station1, station3, station2);
    }

    @Test
    void getStations() {
        sections.addSection(new Section(station1, station4, 20));
        sections.addSection(new Section(station4, station3, 10));
        sections.addSection(new Section(station3, station2, 2));

        section = new Section(line, upStation, downStation, 10);

        assertThat(sections.getStations())
                .containsExactly(station1, station4, station3, station2);
    }
}
