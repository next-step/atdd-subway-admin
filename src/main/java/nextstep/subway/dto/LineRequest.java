package nextstep.subway.dto;

import nextstep.subway.domain.Line;

public class LineRequest {
    private String name;
    private String color;
    private int upStationId;
    private int downStationId;
    private int distance;

    public LineRequest(String name, String color, int upStationId, int downStationId, int distance) {
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

    public int getUpStationId() {
        return upStationId;
    }

    public int getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public Line toLine(){
        return new Line(name,color);
    }
}
