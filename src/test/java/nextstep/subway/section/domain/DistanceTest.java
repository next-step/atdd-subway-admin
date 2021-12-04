package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import nextstep.subway.common.exception.SubwayException;

class DistanceTest {

    @DisplayName("거리의 길이가 0 이하로 생성하면 에러가 발생한다")
    @ParameterizedTest
    @ValueSource(ints = {0, -1, Integer.MIN_VALUE})
    void construct_throwErrorWhenLowerThanOne(int distance) {
        assertThatExceptionOfType(SubwayException.class)
            .isThrownBy(() -> new Distance(distance))
            .withMessage("1 이상의 길이만 입력 가능합니다.");
    }

    @DisplayName("거리 빼기")
    @Test
    void subtract() {
        Distance result = new Distance(10)
            .subtract(new Distance(5));

        assertThat(result)
            .isEqualTo(new Distance(5));
    }

    @DisplayName("거리 더하기")
    @Test
    void add() {
        Distance result = new Distance(10)
            .add(new Distance(5));

        assertThat(result)
            .isEqualTo(new Distance(15));
    }
}