package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class LineTest {

    @DisplayName("노선의 색과 이름을 변경할 수 있다.")
    @Test
    void change() {
        String newName = "다른분당선";
        String newColor = "blue";
        Line line = new Line("name", "red");

        line.changeNameAndColor(newName, newColor);

        assertAll(
                () -> assertThat(line.getName()).isEqualTo(newName),
                () -> assertThat(line.getColor()).isEqualTo(newColor)
        );
    }

    @DisplayName("노선의 이름은 공백을 허용하지 않는다.")
    @NullSource
    @ValueSource(strings = {"", " ", "  "})
    @ParameterizedTest
    void name(String name) {

        assertThatThrownBy(() -> new Line(name, "color"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선의 색은 공백을 허용하지 않는다.")
    @NullSource
    @ValueSource(strings = {"", " ", "  "})
    @ParameterizedTest
    void color(String color) {

        assertThatThrownBy(() -> new Line("name", color))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
