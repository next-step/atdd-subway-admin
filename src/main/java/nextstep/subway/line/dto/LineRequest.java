package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

public class LineRequest {

    private String name;
    private String color;
    private String upStationId;
    private String downStationId;
    private String distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color, String upStationId, String downStationId,
        String distance) {
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

    public Long getUpStationId() {
        return Long.valueOf(upStationId);
    }

    public Long getDownStationId() {
        return Long.valueOf(downStationId);
    }

    public String getDistance() {
        return distance;
    }

    public Line toLine() {
        return new Line(name, color);
    }
}
