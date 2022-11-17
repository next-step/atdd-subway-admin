package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DistanceTest {

    @Test
    void 동등성() {
        assertThat(Distance.of(5L)).isEqualTo(Distance.of(5L));
        assertThat(Distance.of(5L)).isNotEqualTo(Distance.of(10L));
    }

    @Test
    void 빼기() {
        assertThat(Distance.of(10L).sub(Distance.of(3L))).isEqualTo(Distance.of(7L));
    }
}
