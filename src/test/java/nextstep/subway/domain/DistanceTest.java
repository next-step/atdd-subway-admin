package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DistanceTest {

    @Test
    @DisplayName("구간 거리 변경 성공")
    void change() {
        Distance distance = new Distance(5);

        distance.change(10);

        assertThat(distance.getValue()).isEqualTo(10);
    }

    @Test
    @DisplayName("기준거리 보다 작거나 같으면 true 반환")
    void isEqualToOrLessThan() {
        Distance distance = new Distance(5);

        assertThat(distance.isEqualToOrLessThan(8)).isTrue();
    }
}
