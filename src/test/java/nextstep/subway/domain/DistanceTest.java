package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class DistanceTest {

    @ParameterizedTest(name = "구간 지정시 거리는 최소 1 이상이다")
    @ValueSource(longs = {0, -1})
    void constructorFailTest(final long distance) {
        // given & when & then
        assertThatIllegalArgumentException().isThrownBy(
                () -> new Distance(distance)
        );
    }

    @ParameterizedTest(name = "구간과 구간의 거리 길이 차이를 반환한다")
    @CsvSource(value = {"5:1:4", "5:4:1"}, delimiter = ':')
    void validDistanceSubtractTest(final long currDistance, final long newDistance, final long result) {
        // given
        final Distance 기존_거리 = new Distance(currDistance);
        final Distance 변경하려는_거리 = new Distance(newDistance);
        final Distance expected = new Distance(result);

        // when
        final Distance actual = 기존_거리.subtractDistance(변경하려는_거리);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest(name = "구간 추가시 추가하는 구간의 거리는 현재 구간의 거리보다 작아야 한다")
    @CsvSource(value = {"1:2", "1:5", "5:5"}, delimiter = ':')
    void invalidDistanceSubtractTest(final long currDistance, final long newDistance) {
        // given
        final Distance 기존_거리 = new Distance(currDistance);
        final Distance 변경하려는_거리 = new Distance(newDistance);

        // when
        assertThatIllegalArgumentException().isThrownBy(
                () -> 기존_거리.subtractDistance(변경하려는_거리)
        );
    }

    @ParameterizedTest(name = "구간과 구간의 거리 길이 합을 반환한다")
    @CsvSource(value = {"1:1", "2:2", "1:3", "3:1"}, delimiter = ':')
    void addDistanceTest(final long currDistance, final long newDistance) {
        // given
        final Distance 기존_거리 = new Distance(currDistance);
        final Distance 추가하는_거리 = new Distance(newDistance);
        final Distance expected = new Distance(currDistance + newDistance);

        // when
        final Distance actual = 기존_거리.addDistance(추가하는_거리);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
