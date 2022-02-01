package nextstep.subway.line.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.Test;

class SectionsTest {

    public static final Sections sections = new Sections(Arrays.asList(SectionTest.SECTION_2, SectionTest.SECTION_1));

    @Test
    void add_section() {
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

}