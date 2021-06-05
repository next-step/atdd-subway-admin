package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {

    @DisplayName("Section들의 리스트를 보관하는 일급컬렉션")
    @Test
    void create() {

        Station 상행선 = new Station("상행선");
        Station 하행선 = new Station("하행선");
        int 구간거리 = 200;

        Section section = Section.create(상행선, 하행선, 구간거리);
        Sections sections = new Sections();
        sections.addSection(section);

        List<Section> getSections = sections.getSections();

        assertThat(getSections).containsExactly(section);
    }
}