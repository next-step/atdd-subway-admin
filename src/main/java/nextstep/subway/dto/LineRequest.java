package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineStations;

public class LineRequest {

    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    private LineRequest() {}

    public LineRequest(String name,
                       String color,
                       Long upStationId,
                       Long downStationId,
                       int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line toLine(LineStations lineStations) {
        return new Line(name, color, lineStations);
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

}
