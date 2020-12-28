package nextstep.subway.line.domain;

import nextstep.subway.line.exception.DistanceOutOfRangeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DistanceTest {

    @DisplayName("거리는 0과 음수가 아닌 숫자이다.")
    @ParameterizedTest
    @ValueSource(ints = {10, 100})
    void crate(int value) {
        // when
        Distance distance = Distance.valueOf(value);

        // then
        assertThat(distance).isNotNull();
    }

    @DisplayName("길이는 0이거나 음수 값일 수 없다.")
    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void createUnderRange(int value) {
        // when / then
        assertThrows(DistanceOutOfRangeException.class, () -> Distance.valueOf(value));
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

    @DisplayName("다른 거리 값을 뺄수 있다.")
    @ParameterizedTest
    @CsvSource(value = {"10,5,5", "1000,330,670"})
    void subtract(int value1, int value2, int expected) {
        // given
        Distance distance1 = Distance.valueOf(value1);
        Distance distance2 = Distance.valueOf(value2);
        Distance result = Distance.valueOf(expected);

        // when
        Distance actual = distance1.subtract(distance2);

        // then
        assertThat(actual).isEqualTo(result);
    }
}
