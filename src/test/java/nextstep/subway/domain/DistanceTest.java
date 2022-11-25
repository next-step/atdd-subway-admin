package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class DistanceTest {

    @DisplayName("거리를 생성한다.")
    @Test
    void createDistance() {
        Distance distance = new Distance(1);
        assertThat(distance.value()).isEqualTo(1);
    }

    @DisplayName("거리는 1보다 작을 수 없습니다.")
    @Test
    void createDistance_exception() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Distance(0));
    }

    @DisplayName("거리를 더한다.")
    @Test
    void add() {
        Distance distance = new Distance(1);
        assertThat(distance.add(5).value()).isEqualTo(6);
    }

    @DisplayName("거리를 뺀다.")
    @Test
    void subtract() {
        Distance distance = new Distance(5);
        assertThat(distance.subtract(4).value()).isEqualTo(1);
    }

    @DisplayName("거리를 빼는 경우 1보다 작으면 예외가 발생한다.")
    @Test
    void subtract_exception() {
        Distance distance = new Distance(1);
        assertThatIllegalArgumentException().isThrownBy(() -> distance.subtract(4).value());
    }
}
