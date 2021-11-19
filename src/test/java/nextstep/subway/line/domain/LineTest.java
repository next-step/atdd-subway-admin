package nextstep.subway.line.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class LineTest {
    @Test
    void testUpdate() {
        Line line = new Line("박달-강남선", "blue");
        Line toBeLine = new Line("박달-강남선", "blue");
        Line updatedLine = line.update(toBeLine);
        assertAll(
                () -> assertThat(updatedLine).isEqualTo(line),
                () -> assertThat(updatedLine.getName()).isEqualTo(toBeLine.getName()),
                () -> assertThat(updatedLine.getColor()).isEqualTo(toBeLine.getColor())
        );
    }
}
