package nextstep.subway.line;

public class LineFixture {
    private final String name;
    private final String color;
    private final long upStationId;
    private final long downStationId;
    private final double distance;

    private LineFixture(String name, String color, long upStationId, long downStationId, double distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static LineFixture createOf(final String name, final String color, final
    long upStationId, final long downStationId, final double distance) {
        return new LineFixture(name, color, upStationId, downStationId, distance);
    }

    public static LineFixture updateOf(final String name, final String color) {
        return new LineFixture(name, color, 0, 0, 0);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public double getDistance() {
        return distance;
    }
}
