package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DistanceTest {

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -2})
    void 구간_길이는_0보다_커야한다(int distance) {
        assertThatIllegalArgumentException().isThrownBy(() -> new Distance(distance));
        assertThatIllegalArgumentException().isThrownBy(() -> new Distance(1).validateLength(distance));
    }

    @Test
    void 구간_길이보다_크거나_같은지_확인한다() {
        assertThat(new Distance(10).isGreaterEqual(11)).isTrue();
        assertThat(new Distance(10).isGreaterEqual(10)).isTrue();
        assertThat(new Distance(10).isGreaterEqual(9)).isFalse();
    }

    @Test
    void 구간_길이의_차이는_양수로_계산된다() {
        Distance distance = new Distance(10);

        distance.setMinusDistance(15);

        assertThat(distance.getDistance()).isEqualTo(5);
    }
}
