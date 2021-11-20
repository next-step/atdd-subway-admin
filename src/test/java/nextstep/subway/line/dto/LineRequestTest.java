package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class LineRequestTest {
    @Test
    void testToLine() {
        LineRequest request = new LineRequest("박달강남선", "blue");
        Line line = request.toLine();
        assertAll(
                () -> assertThat(line.getName()).isEqualTo(request.getName()),
                () -> assertThat(line.getColor()).isEqualTo(request.getColor())
        );
    }
}
