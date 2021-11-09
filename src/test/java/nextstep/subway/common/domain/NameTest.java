package nextstep.subway.common.domain;

import static org.assertj.core.api.Assertions.assertThatNoException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("이름")
class NameTest {

    @ParameterizedTest(name = "[{index}] {argumentsWithNames} 으로 객체화 가능")
    @DisplayName("객체화")
    @ValueSource(strings = {"name", "이름", " "})
    @NullAndEmptySource
    void instance(String name) {
        assertThatNoException()
            .isThrownBy(() -> Name.from(name));
    }

}
