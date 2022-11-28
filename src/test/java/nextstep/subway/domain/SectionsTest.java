package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class SectionsTest {

    @Test
    @DisplayName("중간에 있는 역을 삭제하면 Section이 수정됨.")
    void delete() {
        Station middleStation = new Station(1L,"중간역");
        Station upperStation = new Station(2L,"상행역");
        Station downStation = new Station(3L,"하행역");
        Line line = new Line("라인","색상",100L);

        Sections sections = new Sections();
        sections.add(Section.of(line,upperStation,middleStation,40L));
        sections.add(Section.of(line,middleStation,downStation,60L));

        sections.delete(middleStation);

        assertThat(sections.hasOnlyOneSection()).isTrue();
    }

    @Test
    @DisplayName("상행역을 가진 Section을 반환하고 Sections에서는 제외됨.")
    void popUpperStationIs() {
        Sections sections = new Sections();
        Section mockSection1 = mock(Section.class);
        Section mockSection2 = mock(Section.class);

        givenForUpperStation(sections, mockSection1, mockSection2);

        Station station = new Station(1L,"상행역");
        Section section = sections.popUpperStationIs(station);

        assertThat(section).isEqualTo(mockSection1);
        assertThat(sections.hasOnlyOneSection()).isTrue();
    }

    @Test
    @DisplayName("하행역을 가진 Section을 반환하고 Sections에서는 제외됨.")
    void popDownStationIs() {
        Sections sections = new Sections();
        Section mockSection1 = mock(Section.class);
        Section mockSection2 = mock(Section.class);

        givenForDownStation(sections, mockSection1, mockSection2);

        Station station = new Station(1L,"하행역");
        Section section = sections.popDownStationIs(station);

        assertThat(section).isEqualTo(mockSection2);
        assertThat(sections.hasOnlyOneSection()).isTrue();
    }

    private static void givenForDownStation(Sections sections, Section mockSection1, Section mockSection2) {
        given(mockSection1.hasDownStation(any())).willReturn(false);
        sections.add(mockSection1);
        given(mockSection2.hasDownStation(any())).willReturn(true);
        sections.add(mockSection2);
    }

    private static void givenForUpperStation(Sections sections, Section mockSection1, Section mockSection2) {
        given(mockSection1.hasUpStation(any())).willReturn(true);
        sections.add(mockSection1);
        given(mockSection2.hasUpStation(any())).willReturn(false);
        sections.add(mockSection2);
    }
}