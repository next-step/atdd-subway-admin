package nextstep.subway.line.domain;

import nextstep.subway.line.exception.DistanceOutOfRangeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DistanceTest {

    @DisplayName("거리는 0이상의 숫자이다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 10})
    void crate(int value) {
        // when
        Distance distance = Distance.valueOf(value);

        // then
        assertThat(distance).isNotNull();
    }

    @DisplayName("길이는 음수 값일 수 없다.")
    @Test
    void createUnderRange() {
        // given
        int negative = -1;

        // when / then
        assertThrows(DistanceOutOfRangeException.class, () -> Distance.valueOf(negative));
    }

    @DisplayName("거리 값이 같으면 동등성을 보장한다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 10})
    void equals(int value) {
        // when
        Distance distance1 = Distance.valueOf(value);
        Distance distance2 = Distance.valueOf(value);

        // then
        assertThat(distance1).isEqualTo(distance2);
    }
}
