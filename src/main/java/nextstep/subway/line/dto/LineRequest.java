package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    public LineRequest() {}

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public LineRequest upStationId(Long upStationId) {
        this.upStationId = upStationId;
        return this;
    }

    public LineRequest downStationId(Long downStationId) {
        this.downStationId = downStationId;
        return this;
    }

    public LineRequest distance(Integer distance) {
        this.distance = distance;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Line toLine() {
        return new Line(name, color);
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

}
