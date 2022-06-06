package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.subway.line.LineDistance;
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
}
