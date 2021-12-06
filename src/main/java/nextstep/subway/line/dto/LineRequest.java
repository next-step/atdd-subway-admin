package nextstep.subway.line.dto;

import nextstep.subway.common.domain.Name;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

public class LineRequest {
    private Name name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color) {
        this.name = new Name(name);
        this.color = color;
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = new Name(name);
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public String getName() {
        return name.printName();
    }

    public String getColor() {
        return color;
    }

    public Long getUpStationId() {
        return this.upStationId;
    }

    public Long getDownStationId() {
        return this.downStationId;
    }

    public Integer getDistance() {
        return this.distance;
    }

    public Line toLine() {
        return new Line(this.name.printName(), this.color);
    }

    public Line toLine(Station upStation, Station downStation) {
        return new Line(this.name.printName(), this.color, upStation, downStation, this.distance);
    }
}
