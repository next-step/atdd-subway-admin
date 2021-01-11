package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color, String upStationId, String downStationId, String distance) {
        this.name = name;
        this.color = color;
        this.upStationId = Long.parseLong(upStationId);
        this.downStationId = Long.parseLong(downStationId);
        this.distance = Integer.parseInt(distance);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public Line toLine() {
        return new Line(name, color, upStationId, downStationId);
    }
}
