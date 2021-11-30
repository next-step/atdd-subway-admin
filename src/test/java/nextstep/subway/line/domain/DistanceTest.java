package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.subway.common.exception.IllegalDistanceException;
import org.junit.jupiter.api.Test;

class DistanceTest {

    @Test
    void test_올바르지_않은_거리는_예외_발생() {
        int nonValidDistance = 0;

        assertThatThrownBy(() -> Distance.of(nonValidDistance))
            .isInstanceOf(IllegalDistanceException.class);
    }

    @Test
    void test_더_멀거나_같은_거리를_빼려고_하면_예외_발생() {
        Distance distance = Distance.of(3);

        assertAll(
            () -> assertThatThrownBy(() -> distance.subtract(Distance.of(4)))
                .isInstanceOf(IllegalDistanceException.class),
            () -> assertThatThrownBy(() -> distance.subtract(Distance.of(3)))
                .isInstanceOf(IllegalDistanceException.class)
        );
    }
}