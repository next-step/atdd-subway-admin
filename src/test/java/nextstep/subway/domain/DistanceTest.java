package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DistanceTest {

    @Test
    @DisplayName("두 거리 객체가 같은지 검증")
    void verifySameDistanceObject() {
        Distance distance = Distance.of(10L);

        assertThat(distance).isEqualTo(Distance.of(10L));
    }

    @ParameterizedTest(name = "음수나 0인 값({0}) 들어오면 예외가 발생")
    @ValueSource(longs = {-5L, 0L})
    void inputInvalidDistance(Long invalid) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Distance.of(invalid))
                .withMessage("거리는 0 이하 일 순 없습니다.");
    }

    @Test
    @DisplayName("두 거리를 더한 값을 확인")
    void addDistance() {
        Distance distance1 = Distance.of(10L);
        Distance distance2 = Distance.of(5L);

        assertThat(Distance.add(distance1, distance2)).isEqualTo(Distance.of(15L));
    }

    @Test
    @DisplayName("두 거리를 뺀 값을 확인")
    void subtractDistance() {
        Distance distance1 = Distance.of(15L);
        Distance distance2 = Distance.of(5L);

        assertThat(Distance.subtract(distance1, distance2)).isEqualTo(Distance.of(10L));
    }

    @Test
    @DisplayName("두 거리를 뺀 값이 음수면 예외 발생")
    void invalidSubtractDistance() {
        Distance distance1 = Distance.of(5L);
        Distance distance2 = Distance.of(10L);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Distance.subtract(distance1, distance2));
    }
}
