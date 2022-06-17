package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("거리")
class DistanceTest {
    @Test
    @DisplayName("거리를 생성할 수 있다.")
    void 거리_생성_성공() {
        assertThat(new Distance(10)).isNotNull();
    }

    @Test
    @DisplayName("거리는 0보다 작을 수 없다.")
    void 거리_생성_실패() {
        assertThatThrownBy(() -> new Distance(-1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("두 개의 거리를 더하면 두 거리의 합을 가진 새로운 거리를 생성한다.")
    void 거리_더하기() {
        Distance 거리1 = new Distance(1);
        Distance 거리2 = new Distance(2);

        assertThat(거리1.add(거리2)).isNotNull().isEqualTo(new Distance(3));
    }

    @Test
    @DisplayName("두 거리를 비교하여 어떤 거리가 더 긴지 확인할 수 있다.")
    void 거리_비교() {
        Distance 짧은_거리 = new Distance(1);
        Distance 긴_거리 = new Distance(2);

        assertAll(() -> assertThat(짧은_거리.isGreaterThan(긴_거리)).isFalse(),
                () -> assertThat(긴_거리.isGreaterThan(짧은_거리)).isTrue());
    }
}