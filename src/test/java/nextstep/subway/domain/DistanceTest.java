package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class DistanceTest {

    @ParameterizedTest
    @ValueSource(longs = {-100000, -1, 0})
    @DisplayName("Distance 생성 시, 거리 값이 0보다 작거나 같을 경우 에러를 반환한다")
    void throw_exception_if_distance_lower_or_equals_zero(Long distance) {
        // When && Then
        assertThatThrownBy(() -> Distance.from(distance))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"10, 10", "1, 2", "1000, 1001"})
    @DisplayName("Distance 차감 시, 결과값이 음수 또는 0이면, 에러를 반환한다")
    void throw_exception_if_subtract_result_is_negative_or_zero(Long sourceValue,
                                                                Long targetValue) {
        // Given
        Distance sourceDistance = Distance.from(sourceValue);
        Distance targetDistance = Distance.from(targetValue);

        // When && Then
        assertThatThrownBy(() -> sourceDistance.subtract(targetDistance))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
