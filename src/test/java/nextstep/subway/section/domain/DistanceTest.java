package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DistanceTest {

    @DisplayName("Distance 는 1 이상으로 생성할 수 있다.")
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 10, 100})
    void create1(int distance) {
        // when & then
        assertThatNoException().isThrownBy(() -> Distance.from(distance));
    }

    @DisplayName("Distanc 는 1 보다 작은 값으로 생성할 수 없다.")
    @ParameterizedTest
    @ValueSource(ints = {-10, -2, -1, 0})
    void create2(int distance) {
        // when & then
        assertThatIllegalArgumentException().isThrownBy(() -> Distance.from(distance))
                                            .withMessageContaining("거리의 길이는 1보다 길어야합니다.");
    }
}