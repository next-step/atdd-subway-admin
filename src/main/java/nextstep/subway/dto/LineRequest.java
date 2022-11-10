package nextstep.subway.dto;

import nextstep.subway.domain.Line;

public class LineRequest {
    private String name;
    private String color;
    private Long distance;
    private Long upStationId;
    private Long downStationId;

    public LineRequest() {
    }

    public LineRequest(String name, String color, Long distance, Long upStationId, Long downStationId) {
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public Line toLine() {
        return new Line(this.name, this.color, this.distance);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getDistance() {
        return distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }
}
