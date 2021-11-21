package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SectionTest {

    @Test
    @DisplayName("지하철 노선을 저장한다.")
    void updateLine() {
        // given
        Line line = new Line("신분당선", "bg-red-600");
        Section section = new Section();

        // when
        section.updateLine(line);

        // then
        assertThat(section.getLine()).isEqualTo(line);
    }
}