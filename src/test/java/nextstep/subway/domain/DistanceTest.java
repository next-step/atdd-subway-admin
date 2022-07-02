package nextstep.subway.domain;

import nextstep.subway.exception.InvalidDistanceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Distance 도메인 객체에 대한 테스트")
public class DistanceTest {

    @DisplayName("객체를 생성한다.")
    @Test
    void createDistance() {
        assertThat(new Distance(3L).getDistance()).isEqualTo(3L);
    }

    @DisplayName("1미만으로는 객체를 생성할 수 없다.")
    @Test
    void createDistanceWithZero() {
        assertThatThrownBy(() -> new Distance(0L)).isExactlyInstanceOf(InvalidDistanceException.class);
    }

    @DisplayName("대소비교를 할 수 있다.")
    @Test
    void compareDistance() {
        Distance small = new Distance(3L);
        Distance big = new Distance(5L);
        assertThat(big.isLong(small)).isTrue();
    }

    @DisplayName("값을 더할 수 있다.")
    @Test
    void plushDistance() {
        assertThat(new Distance(3L).plus(new Distance(5L)).getDistance()).isEqualTo(8L);
    }

    @DisplayName("값을 뺄 수 있다.")
    @Test
    void minusDistance() {
        assertThat(new Distance(5L).minus(new Distance(3L)).getDistance()).isEqualTo(2L);
    }
}
