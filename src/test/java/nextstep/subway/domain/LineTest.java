package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LineTest {
    @Test
    void 동등성() {
        assertAll(
            () -> assertThat(new Line(1L, "2호선", "bg-green-600")).isEqualTo(new Line(1L, "5호선", "bg-red-600")),
            () -> assertThat(new Line(1L, "2호선", "bg-green-600")).isNotEqualTo(new Line(2L, "2호선", "bg-green-600"))
        );
    }

    @Test
    void 노선_업데이트() {
        Line line = new Line(1L, "2호선", "bg-green-600");
        line.update("5호선", "bg-red-600");

        assertAll(
            () -> assertThat(line.getName()).isEqualTo("5호선"),
            () -> assertThat(line.getName()).isNotEqualTo("2호선"),
            () -> assertThat(line.getColor()).isEqualTo("bg-red-600"),
            () -> assertThat(line.getColor()).isNotEqualTo("bg-green-600")
        );
    }
}
