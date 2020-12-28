package nextstep.subway.line.domain;

public class LineFixtures {
    public static Line ID1_LINE = new Line(1L, "testName", "testColor");

    public static void clearLineFixtures() {
        ID1_LINE = new Line(1L, "testName", "testColor");
    }
}
