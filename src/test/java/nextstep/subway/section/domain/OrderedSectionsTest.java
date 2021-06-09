package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderedSectionsTest {

    private List<Section> sections = new ArrayList<>();
    private Section section1;
    private Section section2;
    private Section section3;
    private Section section4;
    private Section section5;

    @BeforeEach
    void setup() {
        Station station1 = Station.of("역1");
        Station station2 = Station.of("역2");
        Station station3 = Station.of("역3");
        Station station4 = Station.of("역4");
        Station station5 = Station.of("역5");
        Station station6 = Station.of("역6");
        int distance = 5;

        // expect 1 - 2 - 4 - 3 - 6
        section1 = Section.of(station1, station2, distance);
        section2 = Section.of(station2, station4, distance);
        section3 = Section.of(station4, station3, distance);
        section4 = Section.of(station3, station6, distance);
        section5 = Section.of(station6, station5, distance);

        // add 4 - 3 - 2 - 1 - 6
        sections.add(section3);
        sections.add(section4);
        sections.add(section2);
        sections.add(section1);
        sections.add(section5);
    }

    @DisplayName("상행종점역을 포함하는 Section을 찾는다.")
    @Test
    void findTopSection() {
        Section topSection = OrderedSections.of(sections).findTopSection();

        assertThat(topSection.getUpStation().getName()).isEqualTo(section1.getUpStation().getName());
        assertThat(topSection.getDownStation().getName()).isEqualTo(section1.getDownStation().getName());
    }

    @DisplayName("정렬된 Sections를 만들어, 정렬된 것을 확인한다.")
    @Test
    void create() {
        OrderedSections orderedSections = OrderedSections.of(sections);

        // actual 1 - 2 - 4 - 3 - 6
        assertThat(orderedSections.get().get(0).getUpStation().getName()).isEqualTo(section1.getUpStation().getName());
        assertThat(orderedSections.get().get(1).getUpStation().getName()).isEqualTo(section2.getUpStation().getName());
        assertThat(orderedSections.get().get(2).getUpStation().getName()).isEqualTo(section3.getUpStation().getName());
        assertThat(orderedSections.get().get(3).getUpStation().getName()).isEqualTo(section4.getUpStation().getName());
        assertThat(orderedSections.get().get(4).getUpStation().getName()).isEqualTo(section5.getUpStation().getName());
    }

}
