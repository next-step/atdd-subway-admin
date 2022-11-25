package nextstep.subway.line;

import nextstep.subway.common.domain.Name;
import nextstep.subway.line.domain.Color;
import nextstep.subway.line.domain.Line;

public class LineFixture {

    public static final String 이호선_이름 = "2호선";
    public static final String 이호선_색상 = "green";
    public static final String 신분당선_이름 = "신분당선";
    public static final String 신분당선_색상 = "red";
    public static final String LINE_NAME_A = "A";
    public static final String LINE_NAME_B = "B";
    public static final String LINE_COLOR_A = "red";
    public static final String LINE_COLOR_B = "blue";

    public static Line 신분당선() {
        return new Line(1L, new Name(신분당선_이름), new Color(신분당선_색상));
    }
    public static Line 이호선() {
        return new Line(2L, new Name(이호선_이름), new Color(이호선_색상));
    }

    public static Line lineA() {
        return new Line(2L, new Name(LINE_NAME_A), new Color(LINE_COLOR_A));
    }

    public static Line lineB() {
        return new Line(2L, new Name(LINE_NAME_B), new Color(LINE_COLOR_B));
    }

}
