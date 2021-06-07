package nextstep.subway.line.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    @Test
    void create() {
        //given
        String expectedName = "1호선";
        String expectedColor = "blue";

        //when
        Line actual = new Line(expectedName, expectedColor);

        //then
        assertThat(actual.getName()).isEqualTo(expectedName);
        assertThat(actual.getColor()).isEqualTo(expectedColor);
    }

    @Test
    void update() {
        //given
        Line line = new Line("1호선", "blue");
        Line newLine = new Line("2호선", "green");

        //when
        line.update(newLine);

        //then
        assertThat(line.getName()).isEqualTo(newLine.getName());
        assertThat(line.getColor()).isEqualTo(newLine.getColor());
    }
}