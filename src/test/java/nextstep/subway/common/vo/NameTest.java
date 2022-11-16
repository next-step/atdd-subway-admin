package nextstep.subway.common.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static nextstep.subway.common.vo.Name.NULL_AND_EMPTY_EXCEPTION_MESSAGE;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("이름")
class NameTest {

    @DisplayName("이름 생성")
    @Test
    void constructor() {
        assertThatNoException().isThrownBy(() -> new Name("2호선"));
    }

    @DisplayName("null 이거나 empty 일 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void null_empty(String name) {
        assertThatThrownBy(() -> new Name(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(NULL_AND_EMPTY_EXCEPTION_MESSAGE);
    }
}
