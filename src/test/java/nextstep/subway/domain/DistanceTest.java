package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DistanceTest {
    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void 길이는_0_이하일_수_없다(int value) {
        assertThatThrownBy(() -> Distance.from(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Distance는 0이하의 값일 수 없습니다.");
    }

    @Test
    void 동등성_비교() {
        assertThat(Distance.from(1)).isEqualTo(Distance.from(1));
    }

    @Test
    void 길이를_뺄_수_있다() {
        Distance distance = Distance.from(7);
        Distance anotherDistance = Distance.from(3);
        Distance actual = Distance.from(4);

        assertThat(distance.subtract(anotherDistance)).isEqualTo(actual);
    }

    @Test
    void 길이를_뺀_결과는_0이하일_수_없다() {
        Distance distance = Distance.from(3);
        Distance anotherDistance = Distance.from(7);
        assertThatThrownBy(() -> distance.subtract(anotherDistance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Distance는 0이하의 값일 수 없습니다.");
    }
}
