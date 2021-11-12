package nextstep.subway.line.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("거리 테스트")
class DistanceTest {

    @Test
    @DisplayName("1보다 작은 거리로 생성하면 예외가 발생한다.")
    void create() {
        // when & then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Distance(0))
                .withMessageMatching("거리는 0보다 커야 합니다.");
    }
}
