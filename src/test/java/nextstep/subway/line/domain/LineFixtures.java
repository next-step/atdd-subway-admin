package nextstep.subway.line.domain;

public class LineFixtures {
    public static Line ID1_LINE = new Line(1L, "testName", "testColor");

    public static Line createLineFixture(
            final Long id, final String name, final String color, final Long upStationId,
            final Long downStationId, final Long distance
    ) {
        Line line = new Line(id, name, color);
        line.initFirstSection(upStationId, downStationId, distance);

        return line;
    }
}
