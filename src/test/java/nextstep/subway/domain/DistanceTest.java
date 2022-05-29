package nextstep.subway.domain;


import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
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
}
