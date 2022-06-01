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
}
