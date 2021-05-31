package nextstep.subway.line.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    private Line line;

    @BeforeEach
    void setUp() {
        line = new Line("2호선", "green");
    }

    @DisplayName("노선을 생성하여 생성된 값을 리턴할 수 있다.")
    @Test
    void create() {
        Line line = new Line("2호선", "green");

        assertThat(line.getName()).isEqualTo("2호선");
        assertThat(line.getColor()).isEqualTo("green");
    }

    @DisplayName("주어진 노선을 새로운 노선으로 갱신한다.")
    @Test
    void update() {
        // given
        Line line = new Line("3호선", "orange");

        // when
        line.update(new Line("5호선", "purple"));

        // then
        assertThat(line.getName()).isEqualTo("5호선");
        assertThat(line.getColor()).isEqualTo("purple");
    }
}
