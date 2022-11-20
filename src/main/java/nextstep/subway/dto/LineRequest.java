package nextstep.subway.dto;

import nextstep.subway.domain.Line;

public class LineRequest {
    private String name;
    private String color;
    private long upStationId;
    private long downStatsionId;
    private long distance;

    public Line toLine() {
        return new Line(name, color);
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

    public long getDownStatsionId() {
        return downStatsionId;
    }

    public long getDistance() {
        return distance;
    }
}
