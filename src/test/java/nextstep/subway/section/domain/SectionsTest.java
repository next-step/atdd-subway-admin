package nextstep.subway.section.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {

    @Test
    @DisplayName("지하철 구간을 추가한다.")
    void add() {
        // given
        Sections sections = new Sections();
        Section section = new Section();

        // when
        sections.add(section);

        // then
        assertThat(sections.getSections().get(0)).isEqualTo(section);
    }
}