package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class LineColorTest {

    @DisplayName("노선(Line) 색깔은 1자 이상으로 생성할 수있다.")
    @ParameterizedTest
    @CsvSource(value = {"a", "a1", "a1!", "연보라색", "a1!가"})
    void create1(String color) {
        // when & then
        assertThatNoException().isThrownBy(() -> LineColor.from(color));
    }

    @DisplayName("노선(Line) 색깔을 null 또는 빈 문자열로 만들면 예외가 발생한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void create2(String color) {
        // when & then
        assertThatIllegalArgumentException()
            .isThrownBy(() -> LineColor.from(color))
            .withMessage(String.format("노선색깔이 비어있습니다. color=%s", color));
    }
}