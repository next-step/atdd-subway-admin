package nextstep.subway.section.domain;

import nextstep.subway.common.message.ExceptionMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DistanceTest {

    @DisplayName("거리가 0 이하인 경우 Distance 생성 시 예외발생")
    @ParameterizedTest(name = "{index} | {displayName} | {argumentsWithNames}")
    @ValueSource(ints = {0, -1})
    void distanceLessOrEqualsZero(int input) {
        Assertions.assertThatThrownBy(() -> Distance.from(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith(String.format(ExceptionMessage.INVALID_SECTION_DISTANCE, input));
    }

    @DisplayName("거리값이 0보다 큰 값이면 Distance 객체가 정상적으로 생성된다.")
    @ParameterizedTest(name = "{index} | {displayName} | {argumentsWithNames}")
    @ValueSource(ints = {1, 10, 100, 1000})
    void create(int input) {
        Distance distance = Distance.from(input);

        Assertions.assertThat(distance).isNotNull();
    }

    @DisplayName("거리 값이 다른 Distance 객체는 동등하지 않다.")
    @Test
    void equalsFail() {
        Distance distance1 = Distance.from(10);
        Distance distance2 = Distance.from(11);

        Assertions.assertThat(distance1).isNotEqualTo(distance2);
    }

    @DisplayName("거리 값이 같은 Distance 객체는 동등하다.")
    @Test
    void equalsSuccess() {
        Distance distance1 = Distance.from(10);
        Distance distance2 = Distance.from(10);

        Assertions.assertThat(distance1).isEqualTo(distance2);
    }

    @DisplayName("Distance 객체의 subtract 메소드를 호출하면 두 거리의 차이를 구할 수 있다.")
    @Test
    void subtract() {
        Distance d1 = Distance.from(10);
        Distance d2 = Distance.from(6);

        Distance result = d1.subtract(d2);

        Assertions.assertThat(result).isEqualTo(Distance.from(4));
    }
}
