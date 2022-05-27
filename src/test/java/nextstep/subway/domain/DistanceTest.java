package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DistanceTest {

    @ParameterizedTest
    @DisplayName("거리는 0보다 커야만 합니다.")
    @ValueSource(ints = {-1, -99, 0})
    void valid(int distance) {
        assertThatIllegalArgumentException().isThrownBy(() -> {
            Distance.of(distance);
        });
    }
}
