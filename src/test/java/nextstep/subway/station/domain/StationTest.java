package nextstep.subway.station.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StationTest {
    @Test
    void equals() {
        assertThat(new Station(1L, "강남역")).isEqualTo(new Station(1L, "강남역"));
    }
}
