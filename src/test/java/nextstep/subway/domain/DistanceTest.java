package nextstep.subway.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DistanceTest {

    @ParameterizedTest
    @ValueSource(ints = { -10, -5, 0 })
    void 유효하지_않은_구간거리(int value) {
        assertThatThrownBy(() -> {
            new Distance(value);
        }).isInstanceOf(IllegalArgumentException.class);
    }
}
