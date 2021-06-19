package nextstep.subway.line.domain;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionStatus;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    @Test
    @DisplayName("노선에 하행과 상행구간이 저장된다.")
    void addSections_test() {
        //given
        Line line = new Line("신붇당선", "red");

        Station 판교역 = new Station("판교역");
        Station 이매역 = new Station("이매역");

        Section 상행 = new Section(SectionStatus.UP, 판교역);
        Section 하행 = new Section(SectionStatus.DOWN, 이매역);

        //when
        Line addedSectionLine = line.addUpSectionAndDownSection(상행, 하행);

        //then
        assertThat(addedSectionLine.getSections()).containsExactlyInAnyOrder(
                상행,
                하행
        );
    }
}
