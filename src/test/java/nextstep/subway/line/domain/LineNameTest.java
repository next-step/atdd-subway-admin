package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineNameTest {

    @Test
    @DisplayName("라인 이름 null인 경우 예외")
    void 이름_null() {
        assertThatIllegalArgumentException().isThrownBy(
                () -> LineName.from(null)
            )
            .withMessage("라인 이름은 빈 값일 수 없습니다.");
    }

    @Test
    @DisplayName("라인 이름 빈 문자열인 경우 예외")
    void 이름_빈_문자열() {
        assertThatIllegalArgumentException().isThrownBy(
                () -> LineName.from("")
            )
            .withMessage("라인 이름은 빈 값일 수 없습니다.");
    }

    @Test
    @DisplayName("라인 이름 공백인 경우 예외")
    void 이름_공백() {
        assertThatIllegalArgumentException().isThrownBy(
                () -> LineName.from("   ")
            )
            .withMessage("라인 이름은 빈 값일 수 없습니다.");
    }

    @Test
    @DisplayName("라인 이름 생성")
    void 이름_생성() {
        LineName result = LineName.from("신분당선");

        assertThat(result).isEqualTo(LineName.from("신분당선"));
    }

}