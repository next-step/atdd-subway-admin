package nextstep.subway.line.dto;

import nextstep.subway.common.vo.Color;
import nextstep.subway.common.vo.Name;
import nextstep.subway.line.domain.Line;

public class LineRequest {

    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setUpStationId(Long upStationId) {
        this.upStationId = upStationId;
    }

    public void setDownStationId(Long downStationId) {
        this.downStationId = downStationId;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public Line toLine() {
        return new Line(new Name(this.name), new Color(this.color));
    }
}

