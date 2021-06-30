package nextstep.subway.line.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineTest {

    @Test
    void create() {
        Line line = new Line("4호선", "sky");

        assertAll(
                () -> assertThat(line.getName()).isEqualTo("4호선"),
                () -> assertThat(line.getColor()).isEqualTo("sky")
        );

    }

    @Test
    void update() {
        Line actual = new Line("4호선", "sky");
        Line expected = new Line("5호선", "purple");

        actual.update(expected);
        assertAll(
                () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
                () -> assertThat(actual.getColor()).isEqualTo(expected.getColor())
        );

    }
}