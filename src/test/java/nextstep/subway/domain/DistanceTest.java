package nextstep.subway.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DistanceTest {

    @ParameterizedTest
    @ValueSource(ints = { -10, -5, 0 })
    void 로또_구매_불가한_가격(int value) {
        assertThatThrownBy(() -> {
            new Distance(value);
        }).isInstanceOf(IllegalArgumentException.class);
    }
}