package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.domain.LineStations;


public class LineCreateRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public LineCreateRequest() {
    }

    public LineCreateRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

    public int getDistance() {
        return distance;
    }

    public Line toLine() {
        LineStation upStation = LineStation.ofFirst(this.upStationId);
        LineStation downStation = LineStation.of(this.upStationId, this.downStationId, this.distance);
        return new Line(name, color, new LineStations(upStation, downStation));
    }
}
