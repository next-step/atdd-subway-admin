package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class DistanceTest {

    @Test
    void createTest() {
        assertThatThrownBy(
                () -> Distance.from(-1)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void isBiggerTest() {
        Distance distance = Distance.from(10);
        assertThat(distance.isBiggerAndEqual(Distance.from(11))).isTrue();
    }

    @Test
    void updateDistanceTest() {
        Distance distance = Distance.from(10);
        distance.update(Distance.from(11));
        assertThat(distance).isEqualTo(Distance.from(11));
    }

    @Test
    void minusDistanceTest() {
        Distance distance = Distance.from(10);
        Distance target = distance.minus(Distance.from(3));
        assertThat(target).isEqualTo(Distance.from(7));
    }
}
