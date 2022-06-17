package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("노선 거리")
class LineDistanceTest {
    @Test
    @DisplayName("노선 거리를 생성할 수 있다.")
    void 노선_거리_생성_성공() {
        assertThat(new LineDistance(10)).isNotNull();
    }

    @Test
    @DisplayName("노선 거리는 0보다 작을 수 없다.")
    void 노선_거리_생성_실패() {
        assertThatThrownBy(() -> new LineDistance(-1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("두 개의 노선 거리를 더하면 두 노선 거리의 합을 가진 새로운 노선 거리를 생성한다.")
    void 노선_거리_더하기() {
        LineDistance distance1 = new LineDistance(1);
        LineDistance distance2 = new LineDistance(2);

        assertThat(distance1.add(distance2)).isNotNull().isEqualTo(new LineDistance(3));
    }

    @Test
    @DisplayName("다른 노선과 비교해서 현재 노선의 길이가 더 긴지 확인할 수 있다.")
    void 노선_거리_비교() {
        LineDistance 짧은_노선 = new LineDistance(1);
        LineDistance 긴_노선 = new LineDistance(2);

        assertAll(() -> assertThat(짧은_노선.isGreaterThan(긴_노선)).isFalse(),
                () -> assertThat(긴_노선.isGreaterThan(짧은_노선)).isTrue());
    }
}