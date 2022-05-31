package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class DistanceTest {

    @ParameterizedTest(name = "구간 지정시 거리는 최소 1 이상이다")
    @ValueSource(longs = {0, -1})
    void constructorFailTest(long distance) {
        // given & when & then
        assertThatIllegalArgumentException().isThrownBy(
                () -> new Distance(distance)
        );
    }

    @ParameterizedTest(name = "구간 추가시 구간의 거리는 현재 거리보다 작아야 한다")
    @CsvSource(value = {"5:1:true", "5:4:true", "1:2:false", "1:5:false", "5:5:false"}, delimiter = ':')
    void isValidDistanceTrueTest(long currDistance, long newDistance, boolean result) {
        // given
        Distance distance = new Distance(currDistance);

        // when
        boolean actual = distance.isValidDistance(newDistance);

        // then
        assertThat(actual).isEqualTo(result);
    }
}
