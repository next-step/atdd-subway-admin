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
    @DisplayName("거리를 빼면 정상적인 값이 반환")
    void minusDistance() {
        Distance distance = Distance.of(15L);
        distance.minus(Distance.of(5L));

        assertThat(distance.getDistance()).isEqualTo(10L);
    }

    @Test
    @DisplayName("현재 거리보다 같거나 큰 값을 빼면 예외가 발생")
    void minusMoreThanDistance() {
        Distance distance = Distance.of(10L);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> distance.minus(Distance.of(15L)))
                .withMessage("추가하려는 구간의 거리는 현재 거리보다 작아야 합니다.");
    }
}
