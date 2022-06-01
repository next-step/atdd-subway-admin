package nextstep.subway.section.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class DistanceTest {
    @DisplayName("지하철 구간은 최소 1 이상이어야 한다.")
    @Test
    void test_not_null() {
        assertThatThrownBy(() -> Distance.from(0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("추가된 구간 길이만큼 기존 구간 길이가 감소한다.")
    @Test
    void test_decrease_distance() {
        // given
        Distance distance = Distance.from(10);
        Distance addedDistance = Distance.from(4);
        // when
        distance.decrease(addedDistance);
        // then
        assertThat(distance.get()).isEqualTo(6);
    }
}