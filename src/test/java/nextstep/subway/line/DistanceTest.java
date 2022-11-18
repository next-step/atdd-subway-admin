package nextstep.subway.line;

import nextstep.subway.line.domain.Distance;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatNoException;

@DisplayName("거리")
public class DistanceTest {

    @DisplayName("거리는 0보다 커야한다.")
    @ParameterizedTest
    @ValueSource(ints = {-1})
    void negative(int distance) {
        assertThatNoException().isThrownBy(() -> new Distance(distance));
    }
}
