package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.subway.common.exception.IllegalDistanceException;
import org.junit.jupiter.api.Test;

class DistanceTest {

    @Test
    void test_올바르지_않은_거리는_예외_발생() {
        int nonValidDistance = 0;

        assertThatThrownBy(() -> Distance.of(nonValidDistance))
            .isInstanceOf(IllegalDistanceException.class);
    }
}