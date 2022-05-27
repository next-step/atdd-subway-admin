package nextstep.subway.dto;

import nextstep.subway.domain.Line;

public class LineRequest implements RequestEntity {
    private String id;
    private String name;
    private String color;
    private long upStationId;
    private long downStationId;
    private long distance;

    public LineRequest() {

    }

    public LineRequest(String name, String color) {
        this(name, color, 1, 2, 10);
    }

    public LineRequest(String name, String color, long upStationId, long downStationId, long distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

    public long getDistance() {
        return distance;
    }

    public Line toLine() {
        return new Line(name, color);
    }
}
