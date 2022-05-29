package nextstep.subway.line.domain;

import static nextstep.subway.line.domain.exception.LineExceptionMessage.LINE_NAME_IS_NOT_NULL;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class LineNameTest {

    @DisplayName("지하철 노선의 명은 1자리 이상의 문자로 구성된다.")
    @ParameterizedTest
    @ValueSource(strings = {"신", "신분", "신분당", "신분당선"})
    void generate01(String name) {
        // given & when & then
        assertThatNoException().isThrownBy(() -> LineName.from(name));
    }

    @DisplayName("지하철 노선의 명은 null 이거나 공란일 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void generate02(String name) {
        // given & when & then
        assertThatIllegalArgumentException().isThrownBy(() -> LineName.from(name))
            .withMessageContaining(LINE_NAME_IS_NOT_NULL.getMessage());
    }
}