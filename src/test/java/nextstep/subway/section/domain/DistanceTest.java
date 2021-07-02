package nextstep.subway.section.domain;

import nextstep.subway.section.exception.BelowZeroDistanceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("거리 도메인 테스트")
class DistanceTest {

    @CsvSource(value = {"-20", "-60", "-40"})
    @ParameterizedTest
    void new_예외(int distanceValue) {
        assertThatExceptionOfType(BelowZeroDistanceException.class)
                .isThrownBy(() -> new Distance(distanceValue));
    }

    @CsvSource(value = {"10:20:30", "50:60:110", "30:40:70"}, delimiterString = ":")
    @ParameterizedTest
    void add_성공(int distance1, int distance2, int result) {
        // given
        Distance createdDistance1 = new Distance(distance1);
        Distance createdDistance2 = new Distance(distance2);

        // when, then
        assertDoesNotThrow(() -> createdDistance1.add(createdDistance2));
    }

    @CsvSource(value = {"60:20:40", "110:60:50", "120:40:80"}, delimiterString = ":")
    @ParameterizedTest
    void minus_성공(int distance1, int distance2, int result) {
        // given
        Distance createdDistance1 = new Distance(distance1);
        Distance createdDistance2 = new Distance(distance2);

        // when, then
        assertDoesNotThrow(() -> createdDistance1.minus(createdDistance2));
    }

    @CsvSource(value = {"60:120", "110:160", "120:140"}, delimiterString = ":")
    @ParameterizedTest
    void minus_예외(int distance1, int distance2) {
        // given
        Distance createdDistance1 = new Distance(distance1);
        Distance createdDistance2 = new Distance(distance2);

        // when, then
        assertThatExceptionOfType(BelowZeroDistanceException.class)
                .isThrownBy(() -> createdDistance1.minus(createdDistance2));
    }

    @CsvSource(value = {"20", "60", "40"})
    @ParameterizedTest
    void equals_성공(int distanceValue) {

        // when
        Distance distance1 = new Distance(distanceValue);
        Distance distance2 = new Distance(distanceValue);

        // then
        assertThat(distance1).isEqualTo(distance2);
    }
}