package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatNoException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("색상")
class ColorTest {

    @ParameterizedTest(name = "[{index}] {argumentsWithNames} 으로 객체화 가능")
    @DisplayName("객체화")
    @ValueSource(strings = {"red", "blue", "green"})
    void instance(String value) {
        assertThatNoException()
            .isThrownBy(() -> Color.from(value));
    }

    @ParameterizedTest(name = "[{index}] {argumentsWithNames} 으로 객체화 불가능")
    @DisplayName("비어있는 값으로 객체화하면 IllegalArgumentException")
    @NullAndEmptySource
    void instance_emptyValue_thrownIllegalArgumentException(String value) {
        assertThatIllegalStateException()
            .isThrownBy(() -> Color.from(value))
            .withMessage("color must not be empty");
    }

}
