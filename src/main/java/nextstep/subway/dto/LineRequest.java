package nextstep.subway.dto;

import nextstep.subway.domain.line.Line;

public class LineRequest {

    private String name;
    private String color;
    private int distance;
    private Long upStationId;
    private Long downStationId;

    public LineRequest() {
    }

    public LineRequest(String name, String color, int distance, Long upStationId, Long downStationId) {
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public int getDistance() {
        return distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Line toLine() {
        return Line.create(name, color, distance);
    }
}
