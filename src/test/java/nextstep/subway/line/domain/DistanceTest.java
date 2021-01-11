package nextstep.subway.line.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("거리 관련 기능")
class DistanceTest {

    @DisplayName("`Distance` 생성")
    @ParameterizedTest
    @ValueSource(longs = {1L, Long.MAX_VALUE})
    void createDistance(long value) {
        // When
        Distance distance = new Distance(value);
        // Then
        assertNotNull(distance);
    }

    @DisplayName("`Distance` 생성 예외 발생")
    @ParameterizedTest
    @ValueSource(longs = {Long.MIN_VALUE, -1L})
    void exceptionWithInvalidDistanceValue(long value) {
        // When & Then
        assertThatThrownBy(
                () -> new Distance(value)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("`Distance`가 작거나 같은 지 확인")
    @ParameterizedTest
    @CsvSource(value = {"1,1", "1,2"})
    void isLessThanEqual(long actualValue, long comparedValue) {
        // Given
        Distance compared = new Distance(comparedValue);
        // When
        Distance actual = new Distance(actualValue);
        // Then
        assertTrue(actual.isLessThanEqual(compared));
    }

    @DisplayName("`Distance`의 거리 값 뺄셈")
    @ParameterizedTest
    @CsvSource({"1,1,0", "2,1,1"})
    void minus(long actualValue, long minusValue, long result) {
        // Given
        Distance compared = new Distance(minusValue);
        // When
        Distance actual = new Distance(actualValue);
        // Then
        assertThat(actual.minus(compared)).isEqualTo(result);
    }

    @DisplayName("`Distance`의 거리 값 덧셈")
    @ParameterizedTest
    @CsvSource({"1,2,3", "0,1,1"})
    void plus(long actualValue, long plusValue, long result) {
        // Given
        Distance compared = new Distance(plusValue);
        // When
        Distance actual = new Distance(actualValue);
        // Then
        assertThat(actual.plus(compared)).isEqualTo(result);
    }
}
