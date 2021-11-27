package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.station.domain.Station;

public class LineRequest {
    private Long id;
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private Distance distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        this(name, color);
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = new Distance(distance);
    }

    public String getName() {
        return name;
    }

    public Line toLine(Station upStation, Station downStation) {
        return new Line(name, color, upStation, downStation, distance);
    }

    public Long getId() {
        return id;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public String getColor(){
        return color;
    }

    public int getDistance() {
        return distance.getDistance();
    }
}
