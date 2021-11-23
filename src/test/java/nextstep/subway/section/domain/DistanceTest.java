package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DistanceTest {

    @Test
    @DisplayName("거리 생성시 0인 경우 에러 발생")
    void 거리_0_생성() {
        assertThatIllegalArgumentException().isThrownBy(
                () -> Distance.from(0)
            )
            .withMessage("거리는 0보다 커야 합니다.");
    }

    @Test
    @DisplayName("거리 생성시 음수 경우 에러 발생")
    void 거리_음수_생성() {
        assertThatIllegalArgumentException().isThrownBy(
                () -> Distance.from(-5)
            )
            .withMessage("거리는 0보다 커야 합니다.");
    }

    @Test
    @DisplayName("거리 생성시 0 초과인 경우 정상 동작")
    void 거리_생성() {
        Distance result = Distance.from(5);

        assertThat(result).isEqualTo(Distance.from(5));
    }

}