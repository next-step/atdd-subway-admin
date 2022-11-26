package nextstep.subway.distance;

import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Distances;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

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
        assertThat(distance.getNewSectionDistance(new Distance(3))).isEqualTo(new Distance(7));
    }

    @Test
    @DisplayName("거리가 10인 구간에서 새로운 섹션을 등록할 때 길이가 같거나 클 경우 오류 발생")
    void newSectionDistanceExceptionTest() {
        Distance distance = new Distance(10);
        assertThatThrownBy(() -> distance.getNewSectionDistance(new Distance(10)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> distance.getNewSectionDistance(new Distance(12)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("생성하려는 구간의 길이가 총 길이보다 크거나 같은 경우 오류발생 테스트")
    void LessThanAllSectionDistanceExceptionTest() {
        Distance distance = new Distance(10);
        Distance distance1 = new Distance(3);
        Distance distance2 = new Distance(7);
        Distance distance3 = new Distance(5);
        assertThatThrownBy(() -> new Distances(Arrays.asList(distance1, distance2))
                .checkLessThanAllSectionDistance(distance))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Distances(Arrays.asList(distance1, distance3))
                .checkLessThanAllSectionDistance(distance))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
