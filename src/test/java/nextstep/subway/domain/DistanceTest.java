package nextstep.subway.domain;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class DistanceTest {
    @DisplayName("구간 길이가 0 혹은 0보다 작은 경우 예외 테스트")
    @ParameterizedTest(name = "구간 길이가 {0}인 경우 예외 테스트")
    @ValueSource(strings = {"0", "-1", "-100"})
    void distanceZeroOrLess(int input) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Distance.valueOf(input))
                .withMessage("구간 길이는 0 이하가 될 수 없습니다.");
    }

    @DisplayName("구간길이 끼리는 뺄 수 있다.")
    @ParameterizedTest(name = "구간길이 {0}은 {1}을 빼 {2}가 된다.")
    @CsvSource(value = {"10:5:5", "20:1:19"}, delimiter = ':')
    void minus(int input, int other, int expect) {
        Distance distance = Distance.valueOf(input);
        distance.minus(Distance.valueOf(other));
        assertThat(distance).isEqualTo(Distance.valueOf(expect));
    }

    @DisplayName("구간길이를 빼서 0 또2는 음수가 나올 경우 예외가 발생한다.")
    @ParameterizedTest(name = "구간 {0}은 {1}을 빼 예외가 발생한다.")
    @CsvSource(value = {"10:15", "20:20"}, delimiter = ':')
    void minusZeroOrNegative(int input, int other) {
        Distance distance = Distance.valueOf(input);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> distance.minus(Distance.valueOf(other)))
                .withMessage("구간 길이는 0 이하가 될 수 없습니다.");
    }
}
