package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {

    @Test
    @DisplayName("첫 지하철 구간을 추가한다.")
    void add_first() {
        // given
        Sections sections = new Sections();
        Section section = new Section();

        // when
        sections.add(section);

        // then
        assertThat(sections.getSections().get(0)).isEqualTo(section);
    }

    @Test
    @DisplayName("지하철 구간을 추가한다.")
    void add_not_first() {
        // given
        Sections sections = new Sections();
        Station station = new Station("강남역");
        Section section = new Section(station, new Station("판교역"), 10, null);
        sections.add(section);

        Section newSection = new Section(station, new Station("양재역"), 3, null);

        // when
        sections.add(newSection);

        // then
        assertThat(sections.getSections().size()).isEqualTo(2);
    }
}