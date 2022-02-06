package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationTest;
import org.junit.jupiter.api.Test;

class SectionsTest {

    public static final Sections sections = Sections
        .of(SectionTest.SECTION_2, SectionTest.SECTION_1);

    @Test
    void create_sections() {
        assertThat(sections.getSections().get(0).equals(SectionTest.SECTION_2));
        assertThat(sections.getSections().get(1).equals(SectionTest.SECTION_1));
    }

    @Test
    void get_ordered_section() {
        List<Section> orderedSections = sections.orderedSections();
        assertThat(orderedSections.get(0)).isEqualTo(SectionTest.SECTION_1);
        assertThat(orderedSections.get(1)).isEqualTo(SectionTest.SECTION_2);
    }

    @Test
    void get_ordered_station() {
        List<Station> orderedStation = sections.stations();
        assertThat(orderedStation.get(0)).isEqualTo(SectionTest.SECTION_1.getUpStation());
        assertThat(orderedStation.get(1)).isEqualTo(SectionTest.SECTION_2.getUpStation());
        assertThat(orderedStation.get(2)).isEqualTo(SectionTest.SECTION_2.getDownStation());
    }

    @Test
    void add_section_in_the_middle() {
        //given
        int distance = 3;
        int originalDistance = SectionTest.SECTION_2.getDistance();
        Section section = Section
            .of(LineTest.LINE_2, StationTest.STATION_4, StationTest.STATION_1, distance);

        //when
        sections.addSection(section);
        List<Section> orderedSections = sections.orderedSections();

        //then
        assertThat(sections.getSections().size()).isEqualTo(3);
        assertThat(orderedSections.get(1).getDownStation())
            .isEqualTo(orderedSections.get(2).getUpStation());
        assertThat(orderedSections.get(2).getDistance()).isEqualTo(originalDistance - distance);
    }

    @Test
    void add_first_section() {
        //given
        Section section = Section
            .of(LineTest.LINE_2, StationTest.STATION_1, StationTest.STATION_2, 10);

        //when
        sections.addSection(section);

        //then
        assertThat(sections.getSections().size()).isEqualTo(3);
        assertThat(sections.orderedSections().get(0)).isEqualTo(section);
    }


    @Test
    void add_last_section() {
        //given
        Section section = Section
            .of(LineTest.LINE_2, StationTest.STATION_3, StationTest.STATION_1, 10);

        //when
        sections.addSection(section);

        //then
        assertThat(sections.getSections().size()).isEqualTo(3);
        assertThat(sections.orderedSections().get(2)).isEqualTo(section);
    }


}