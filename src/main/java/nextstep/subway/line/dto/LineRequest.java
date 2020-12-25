package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;


public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color,
                       Long upStationId, Long downStationId,
                       int distance) {
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

    public Line toLineWithStation() {
        return new Line(name, color, getStation(upStationId), getStation(downStationId), distance);
    }
    public Line toLine() {
        return new Line(name, color);
    }

    public Station getStation(Long id) {
        return StationService.findById(id);
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
}
