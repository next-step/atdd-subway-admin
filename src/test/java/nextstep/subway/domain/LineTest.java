package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LineTest {
    private Station GANGNAM;
    private Station JAMSIL;

    @BeforeEach
    void setup() {
        GANGNAM = new Station(1L, "강남역");
        JAMSIL = new Station(2L, "잠실역");
    }

    @Test
    void 동등성() {
        assertAll(
            () -> assertThat(new Line(1L, "2호선", "bg-green-600", GANGNAM, JAMSIL, 10L))
                .isEqualTo(new Line(1L, "5호선", "bg-red-600", JAMSIL, GANGNAM, 10L)),
            () -> assertThat(new Line(1L, "2호선", "bg-green-600", GANGNAM, JAMSIL, 10L))
                .isNotEqualTo(new Line(2L, "2호선", "bg-green-600", GANGNAM, JAMSIL, 10L))
        );
    }

    @Test
    void 노선_업데이트() {
        Line line = new Line(1L, "2호선", "bg-green-600", GANGNAM, JAMSIL, 10L);
        line.update("5호선", "bg-red-600");

        assertAll(
            () -> assertThat(line.getName()).isEqualTo("5호선"),
            () -> assertThat(line.getName()).isNotEqualTo("2호선"),
            () -> assertThat(line.getColor()).isEqualTo("bg-red-600"),
            () -> assertThat(line.getColor()).isNotEqualTo("bg-green-600")
        );
    }
}
