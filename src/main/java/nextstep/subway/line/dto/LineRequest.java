package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public LineRequest() { }

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public String name() {
        return name;
    }

    public String color() {
        return color;
    }

    public Long upStationId() {
        return upStationId;
    }

    public Long downStationId() {
        return downStationId;
    }

    public int distance() {
        return distance;
    }

    public Line toLine() {
        return new Line(name, color);
    }
}
