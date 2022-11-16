package nextstep.subway.line;

import nextstep.subway.common.vo.ColorTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.common.vo.NameTest.신분당선_이름;
import static org.assertj.core.api.Assertions.assertThatNoException;

@DisplayName("노선")
class LineTest {

    public static final Line 신분당선 = new Line(1L, 신분당선_이름, ColorTest.신분당선_색상);

    @DisplayName("노선 생성")
    @Test
    void constructor() {
        assertThatNoException().isThrownBy(() -> new Line(신분당선_이름, ColorTest.신분당선_색상));
    }
}
