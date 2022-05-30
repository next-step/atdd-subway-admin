package nextstep.subway.dto;

import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;

public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private Distance distance;

    public LineRequest(String name, String color, Distance distance, Long upStationId, Long downStationId) {
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

    public Distance getDistance() {
        return distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Line toLine() {
        return new Line(name, color, distance);
    }
}
