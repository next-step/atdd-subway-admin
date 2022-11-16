package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DistanceTest {
    @DisplayName("거리는 0 이하일 수 없다")
    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void valueException(int value) {
        assertThatThrownBy(() -> new Distance(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("거리는 0보다 커야 합니다.");
    }

    @DisplayName("거리를 뺄 수 있다")
    @Test
    void subtract() {
        Distance distance1 = new Distance(10);
        Distance distance2 = new Distance(7);

        Distance result = distance1.subtract(distance2);

        assertThat(result.get()).isEqualTo(3);
    }

    @DisplayName("거리를 더할 수 있다")
    @Test
    void add() {
        Distance distance1 = new Distance(10);
        Distance distance2 = new Distance(20);

        Distance result = distance1.add(distance2);

        assertThat(result.get()).isEqualTo(30);
    }
}
