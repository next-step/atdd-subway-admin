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

    @DisplayName("기존 역 사이의 길이보다 큰 값으로 길이를 감소할 수 없다.")
    @Test
    void test_not_greater_then_distance() {
        // given
        Distance distance = Distance.from(10);
        Distance addedDistance = Distance.from(11);
        // when & then
        assertThatThrownBy(() -> distance.decrease(addedDistance))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("기존 역 사이의 길이와 같은 값으로 길이를 감소할 수 없다.")
    @Test
    void test_not_equals_distance() {
        // given
        Distance distance = Distance.from(10);
        Distance addedDistance = Distance.from(10);
        // when & then
        assertThatThrownBy(() -> distance.decrease(addedDistance))
                .isInstanceOf(IllegalArgumentException.class);
    }
}