package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DistanceTest {

    @ParameterizedTest
    @ValueSource(ints = {0, -1, Integer.MIN_VALUE})
    void 거리의_길이가_0_이하로_생성하면_에러가_발생한다(int distance) {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Distance(distance))
            .withMessage("1 이상의 길이만 입력 가능합니다. distance: " + distance);
    }
}