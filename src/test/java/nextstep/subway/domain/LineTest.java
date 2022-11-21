package nextstep.subway.domain;

import static nextstep.subway.line.LineNameTestFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.StationNameTestFixture;

class LineTest {
    private Station GANGNAM;
    private Station JAMSIL;

    @BeforeEach
    void setup() {
        GANGNAM = new Station(1L, StationNameTestFixture.GANGNAM);
        JAMSIL = new Station(2L, StationNameTestFixture.JAMSIL);
    }

    @Test
    void 동등성() {
        assertAll(
            () -> assertThat(new Line(1L, LINE_2, "bg-green-600", GANGNAM, JAMSIL, 10L))
                .isEqualTo(new Line(1L, LINE_5, "bg-red-600", JAMSIL, GANGNAM, 10L)),
            () -> assertThat(new Line(1L, LINE_2, "bg-green-600", GANGNAM, JAMSIL, 10L))
                .isNotEqualTo(new Line(2L, LINE_2, "bg-green-600", GANGNAM, JAMSIL, 10L))
        );
    }

    @Test
    void 노선_업데이트() {
        Line line = new Line(1L, LINE_2, "bg-green-600", GANGNAM, JAMSIL, 10L);
        line.update(LINE_5, "bg-red-600");

        assertAll(
            () -> assertThat(line.isSameName(LINE_5)).isTrue(),
            () -> assertThat(line.isSameName(LINE_2)).isFalse(),
            () -> assertThat(line.isSameColor("bg-red-600")).isTrue(),
            () -> assertThat(line.isSameColor("bg-green-600")).isFalse()
        );
    }
}
