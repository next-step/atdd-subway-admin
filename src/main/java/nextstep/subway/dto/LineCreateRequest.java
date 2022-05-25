package nextstep.subway.dto;

import nextstep.subway.domain.Line;

public class LineCreateRequest {
    private String name;

    private String color;

    private Long upStationId;

    private Long downStationId;

    private Integer distance;

    public Line toLine() {
        return new Line(this.name, this.color, this.distance);
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

    public Integer getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "LineRequest{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", upStationId=" + upStationId +
                ", downStationId=" + downStationId +
                ", distance=" + distance +
                '}';
    }
}
