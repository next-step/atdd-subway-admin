package nextstep.subway.domain;


import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class StationTest {

    @DisplayName("이름이 공백이면 IllegalArgumentException 이 발생한다.")
    @NullSource
    @ValueSource(strings = {"", " "})
    @ParameterizedTest
    void valid_name(String name) {
        assertThatThrownBy(() -> Station.from(name)).isInstanceOf(IllegalArgumentException.class);
    }
}
