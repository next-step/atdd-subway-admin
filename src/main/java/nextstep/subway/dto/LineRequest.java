package nextstep.subway.dto;

import nextstep.subway.domain.Line;

public class LineRequest {

    private String name;
    private String color;
    private int distance;
    private String upStationId;
    private String downStationId;

    public LineRequest() {
    }

    public LineRequest(String name, String color, int distance, String upStationId, String downStationId) {
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public Line toLine() {
        return new Line(name, color, distance, upStationId, downStationId);
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

    public String getUpStationId() {
        return upStationId;
    }

    public String getDownStationId() {
        return downStationId;
    }
}
