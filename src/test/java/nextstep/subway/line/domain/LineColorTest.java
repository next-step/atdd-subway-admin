package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;

import nextstep.subway.constants.LineExceptionMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class LineColorTest {

    @DisplayName("지하철 노선의 색상은 1자리 이상의 문자로 구성된다.")
    @ParameterizedTest
    @ValueSource(strings = {"r", "re", "red", "deep red"})
    void generate01(String color) {
        // given & when & then
        assertThatNoException().isThrownBy(() -> LineColor.from(color));
    }

    @DisplayName("지하철 노선의 색상은 null 이거나 공란일 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void generate02(String color) {
        // given & when & then
        assertThatIllegalArgumentException().isThrownBy(() -> LineColor.from(color))
            .withMessageContaining(LineExceptionMessage.LINE_COLOR_IS_NOT_NULL);
    }
}