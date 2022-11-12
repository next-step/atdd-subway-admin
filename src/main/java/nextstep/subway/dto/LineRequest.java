package nextstep.subway.dto;

import nextstep.subway.domain.Line;

public class LineRequest {
    private String name;
    private String color;
    private Long upStationsId;
    private Long downStationId;
    private Integer distance;

    public LineRequest(){}

    public LineRequest(String name, String color, Long upStationsId, Long downStationId, Integer distance) {
        this.name = name;
        this.color = color;
        this.upStationsId = upStationsId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line toLine(){
        return new Line(name, color);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getUpStationsId() {
        return upStationsId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
