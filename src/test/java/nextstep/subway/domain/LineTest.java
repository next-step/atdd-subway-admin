package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class LineTest {
    @Test
    void 노선_이름과_색상을_파라미터로_Line_객체가_생성되어야_한다() {
        // given
        final String lineName = "신분당선";
        final String color = "bg-red-600";

        // when
        final Line line = new Line(lineName, color);

        // then
        assertThat(line).isNotNull();
        assertThat(line).isInstanceOf(Line.class);
    }
}
