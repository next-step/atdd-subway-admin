package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class LineNameTest {

    @DisplayName("노선(Line) 이름은 1자 이상으로 생성할 수있다.")
    @ParameterizedTest
    @CsvSource(value = {"가", "가나", "가나다", "가나다1", "가나다12", "가나다12!"})
    void create1(String name) {
        // when & then
        assertThatNoException().isThrownBy(() -> LineName.from(name));
    }

    @DisplayName("노선(Line) 이름을 null 또는 빈 문자열로 만들면 예외가 발생한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void create2(String name) {
        // when & then
        assertThatIllegalArgumentException()
            .isThrownBy(() -> LineName.from(name))
            .withMessage(String.format("노선이름이 비어있습니다. name=%s", name));
    }
}