package nextstep.subway.dto;

import nextstep.subway.domain.Line;

public class LineRequest {

    private String name;
    private String color;
    private int distance;
    private String upStationId;
    private String downStationId;

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
