package nextstep.subway.domain;

import nextstep.subway.exception.CannotAddSectionException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class DistanceTest {

    @Test
    void valueOf() {
        assertThatThrownBy( () -> Distance.valueOf(0))
                .isInstanceOf(IllegalArgumentException.class);
        assertDoesNotThrow( () -> Distance.valueOf(1));
    }

    @Test
    void getValue() {
        Distance distance = Distance.valueOf(1);
        assertThat(distance.getValue()).isEqualTo(1);
    }

    @Test
    void isLessThan() {
        assertThat(Distance.valueOf(2).isLessThan(Distance.valueOf(1))).isFalse();
        assertThat(Distance.valueOf(2).isLessThan(Distance.valueOf(2))).isTrue();
        assertThat(Distance.valueOf(2).isLessThan(Distance.valueOf(3))).isTrue();
    }

    @Test
    void subtract() {
        assertThat(Distance.valueOf(4).subtract(Distance.valueOf(2))).isEqualTo(Distance.valueOf(2));
    }

    @Test
    void cannotSubtractParameterLessThan() {
        assertThatThrownBy(() -> Distance.valueOf(4).subtract(Distance.valueOf(4)))
                .isInstanceOf(CannotAddSectionException.class);
    }

    @Test
    void add() {
        assertThat(Distance.valueOf(4).add(Distance.valueOf(1))).isEqualTo(Distance.valueOf(5));
    }
}