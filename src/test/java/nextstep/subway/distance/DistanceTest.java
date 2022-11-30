package nextstep.subway.distance;

import nextstep.subway.domain.Distance;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DistanceTest {

    @Test
    @DisplayName("거리가 0인 경우 오류 발생")
    void zeroDistanceCreateExceptionTest() {
        assertThatThrownBy(() -> new Distance(0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("거리가 음수인 경우 오류 발생")
    void minusDistanceCreateExceptionTest() {
        assertThatThrownBy(() -> new Distance(-1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("거리가 10인 구간에서 새로운 섹션을 등록할 때 또 다른 섹션의 나머지 거리 구하기")
    void newSectionDistanceTest() {
        Distance distance = new Distance(10);
        assertThat(distance.subtract(new Distance(3))).isEqualTo(new Distance(7));
    }

    @Test
    @DisplayName("거리가 10인 구간에서 새로운 섹션을 등록할 때 길이가 같거나 클 경우 오류 발생")
    void newSectionDistanceExceptionTest() {
        Distance distance = new Distance(10);
        assertThatThrownBy(() -> distance.subtract(new Distance(10)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> distance.subtract(new Distance(12)))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
