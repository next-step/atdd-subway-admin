package nextstep.subway.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class DistanceTest {

    @Test
    void 더하기_수행() {
        Distance of = Distance.of(1L);

        assertThat(of.plus(1L)).isEqualTo(Distance.of(2L));
    }

    @Test
    void 빼기_수행() {
        Distance of = Distance.of(2L);

        assertThat(of.minus(1L)).isEqualTo(Distance.of(1L));
    }

    @Test
    void 거리는_양수여야함() {
        Distance of = Distance.of(1L);

        assertThatThrownBy(() -> of.minus(1L)).isInstanceOf(IllegalArgumentException.class);
    }
}