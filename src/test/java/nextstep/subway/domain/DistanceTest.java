package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class DistanceTest {
    @Test
    void 음수_거리_예외() {
        assertThatThrownBy(() ->
                Distance.from(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
