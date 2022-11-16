package nextstep.subway.line;

import nextstep.subway.common.vo.ColorTest;
import org.junit.jupiter.api.DisplayName;

import static nextstep.subway.common.vo.NameTest.신분당선_이름;

@DisplayName("노선")
class LineTest {

    public static final Line 신분당선 = new Line(1L, 신분당선_이름, ColorTest.신분당선_색상);

}
