package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DistanceTest {

    @DisplayName("거리의 길이가 0 이하로 생성하면 에러가 발생한다")
    @ParameterizedTest
    @ValueSource(ints = {0, -1, Integer.MIN_VALUE})
    void construct_throwErrorWhenLowerThanOne(int distance) {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Distance(distance))
            .withMessage("1 이상의 길이만 입력 가능합니다. distance: " + distance);
    }

    @DisplayName("거리 빼기")
    @Test
    void subtract() {
        Distance result = new Distance(10).subtract(new Distance(5));

        assertThat(result)
            .isEqualTo(new Distance(5));
    }
}