package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineColorTest {

    @Test
    @DisplayName("라인 색상 null인 경우 예외")
    void 색상_null() {
        assertThatIllegalArgumentException().isThrownBy(
                () -> LineColor.from(null)
            )
            .withMessage("라인 색상은 빈 값일 수 없습니다.");
    }

    @Test
    @DisplayName("라인 색상 빈 문자열인 경우 예외")
    void 색상_빈_문자열() {
        assertThatIllegalArgumentException().isThrownBy(
                () -> LineColor.from("")
            )
            .withMessage("라인 색상은 빈 값일 수 없습니다.");
    }

    @Test
    @DisplayName("라인 색상 공백인 경우 예외")
    void 색상_공백() {
        assertThatIllegalArgumentException().isThrownBy(
                () -> LineColor.from("   ")
            )
            .withMessage("라인 색상은 빈 값일 수 없습니다.");
    }

    @Test
    @DisplayName("라인 색상 생성")
    void 색상_생성() {
        LineColor result = LineColor.from("bg-red-600");

        assertThat(result).isEqualTo(LineColor.from("bg-red-600"));
    }

}