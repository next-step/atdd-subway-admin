package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class DistanceTest {

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -2})
    void 구간_길이는_0보다_커야한다(int distance) {
        assertThatIllegalArgumentException().isThrownBy(() -> new Distance(distance));
    }

    @Test
    void 구간_길이보다_크거나_같은지_확인한다() {
        assertThat(new Distance(10).isGreaterEqual(11)).isTrue();
        assertThat(new Distance(10).isGreaterEqual(10)).isTrue();
        assertThat(new Distance(10).isGreaterEqual(9)).isFalse();
    }

    @ParameterizedTest
    @CsvSource(value = {"11:1", "12:2", "5:5"}, delimiter = ':')
    void 구간_길이를_뺀_값의_절대값을_반환한다(int distance, int expected) {
        assertThat(new Distance(10).getMinusDistance(distance)).isEqualTo(expected);
    }
}
