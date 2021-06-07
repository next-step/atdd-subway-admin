package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

public class SectionTest {

    @Test
    void invalid() {
        // given
        final Station station = new Station();

        // then
        assertAll(
            () -> assertThatThrownBy(() -> new Section(null, station, 100))
                .isInstanceOf(IllegalArgumentException.class),
            () -> assertThatThrownBy(() -> new Section(station, null, 100))
                .isInstanceOf(IllegalArgumentException.class),
            () -> assertThatThrownBy(() -> new Section(station, station, 0))
                .isInstanceOf(IllegalArgumentException.class)
        );
    }
}
