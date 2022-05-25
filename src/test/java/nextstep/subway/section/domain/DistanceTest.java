package nextstep.subway.section.domain;

import static nextstep.subway.section.domain.exception.StationExceptionMessage.DISTANCE_IS_MUST_BE_GREATER_THAN_1;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DistanceTest {

    @DisplayName("1 이상의 정수로 거리를 생성할 수 있다.")
    @ParameterizedTest
    @ValueSource(ints = {1, 10, 100, 1000})
    void generate01(int distance) {
        // given & when & then
        assertThatNoException().isThrownBy(() -> Distance.from(distance));
    }

    @DisplayName("1보다 작은 정수로 거리를 생성할 수 없다.")
    @ParameterizedTest
    @ValueSource(ints = {-100, -1, 0})
    void generate02(int distance) {
        // given & when & then
        assertThatIllegalArgumentException().isThrownBy(() -> Distance.from(distance))
            .withMessageContaining(DISTANCE_IS_MUST_BE_GREATER_THAN_1.getMessage());
    }
}