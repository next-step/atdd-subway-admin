package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class DistanceTest {

    @Test
    @DisplayName("Distance 객체는 1이상의 양수를 가진다")
    void constructorTest() {
        // given & when
        Distance distance = new Distance(1);

        // then
        assertThat(distance.getDistance()).isEqualTo(1);
    }

    @ParameterizedTest(name = "Distance 객체는 0미만의 수를 인자로 받으면 오류를 반환한다")
    @ValueSource(longs = {0, -1})
    void constructorFailTest(long distance) {
        // given & when & then
        assertThatIllegalArgumentException().isThrownBy(
                () -> new Distance(distance)
        );
    }

    @ParameterizedTest(name = "현재 거리보다 입력받은 거리가 작으면 참을 반환한다")
    @CsvSource(value = {"5,1", "5,4"}, delimiter = ',')
    void isValidDistanceTrueTest(long currDistance, long newDistance) {
        // given
        Distance distance = new Distance(currDistance);

        // when
        boolean actual = distance.isValidDistance(newDistance);

        // then
        assertThat(actual).isTrue();
    }

    @ParameterizedTest(name = "현재 거리보다 입력받은 거리가 같거나 길면 거짓을 반환한다")
    @CsvSource(value = {"1,2", "1,5", "5,5"}, delimiter = ',')
    void isValidDistanceFalseTest(long currDistance, long newDistance) {
        // given
        Distance distance = new Distance(currDistance);

        // when
        boolean actual = distance.isValidDistance(newDistance);

        // then
        assertThat(actual).isFalse();
    }

}
