package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StationTest {
    @Test
    void 동등성() {
        assertAll(
            () -> assertThat(new Station(1L, "강남역")).isEqualTo(new Station(1L, "역삼역")),
            () -> assertThat(new Station(1L, "강남역")).isNotEqualTo(new Station(2L, "강남역"))
        );
    }
}
