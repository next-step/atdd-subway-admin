package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @DisplayName("노선의 색과 이름을 변경할 수 있다.")
    @Test
    void change() {
        String newName = "다른분당선";
        String newColor = "blue";
        Line line = Line.of("name", "red", null, null, 10);

        line.changeNameAndColor(newName, newColor);

        assertAll(
                () -> assertThat(line.getName()).isEqualTo(newName),
                () -> assertThat(line.getColor()).isEqualTo(newColor)
        );

    }
}
