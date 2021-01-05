package nextstep.subway.section;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionsTest {

    @DisplayName("구간 제거 시 기존 구간 결합")
    @Test
    void testRemoveSectionByStation() {
        // given
        Line line = new Line("신분당선", "bg-red-600");
        Station station1 = new Station("양재");
        Station station2 = new Station("양재시민의숲");
        Station station3 = new Station("청계산입구");
        Sections sections = new Sections();
        sections.add(new Section(line, station1, station2, 4));
        sections.add(new Section(line, station2, station3, 6));

        // when
        sections.removeByStation(line, station2);

        // then
        assertThat(sections.getSections().size()).isEqualTo(1);
        assertThat(sections.getSections().get(0).getDistance()).isEqualTo(10);
        assertThat(sections.getSections().get(0).getUpStation()).isEqualTo(station1);
        assertThat(sections.getSections().get(0).getDownStation()).isEqualTo(station3);
    }
}
