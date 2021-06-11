package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DistanceTest {

    @Test
    @DisplayName("생성 테스트")
    void create() {
        // given & when & then
        assertThat(Distance.of(5).toNumber()).isEqualTo(5);
        assertThat(Distance.of(10).toNumber()).isEqualTo(10);
    }

    @Test
    @DisplayName("유효하지 않은 거리(0보다 작은) 생성 테스트")
    void validate() {
        // given & when & then
        Assertions.assertThrows(IllegalArgumentException.class, () -> Distance.of(-1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> Distance.of(0));
    }

    @Test
    @DisplayName("현재 거리보다 짧은 거리 수정 테스트")
    void updateDiffDistance() {
        // given
        Distance distance = Distance.of(5);
        // when
        distance.subtractDiffDistance(3);
        // then
        assertThat(distance.toNumber()).isEqualTo(2);
    }

    @Test
    @DisplayName("현재 거리보다 같거나 긴 거리 수정 테스트")
    void updateDiffDistanceException() {
        // given
        Distance distance = Distance.of(5);
        // when & then
        Assertions.assertThrows(IllegalArgumentException.class, () -> distance.subtractDiffDistance(10));
        Assertions.assertThrows(IllegalArgumentException.class, () -> distance.subtractDiffDistance(5));
    }
}
