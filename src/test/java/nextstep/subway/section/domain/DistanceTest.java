package nextstep.subway.section.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DistanceTest {
    @DisplayName("지하철 구간은 최소 1 이상이어야 한다.")
    @Test
    void test_not_null() {
        assertThatThrownBy(() -> Distance.from(0))
                .isInstanceOf(IllegalArgumentException.class);
    }
}