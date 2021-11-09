package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("거리")
class DistanceTest {

    @Test
    @DisplayName("객체화")
    void instance() {
        assertThatNoException()
            .isThrownBy(() -> Distance.from(Integer.MAX_VALUE));
    }

    @Test
    @DisplayName("음수로 객체화 하면 IllegalArgumentException")
    void instance_negativeValue_thrownIllegalArgumentException() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> Distance.from(Integer.MIN_VALUE))
            .withMessageMatching("distance value\\(\\d+\\) must be positive");
    }

}
