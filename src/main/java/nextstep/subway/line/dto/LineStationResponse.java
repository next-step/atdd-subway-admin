package nextstep.subway.line.dto;


import nextstep.subway.line.domain.LineStation;
import nextstep.subway.station.dto.StationResponse;

public class LineStationResponse {

    private Long id;
    private String name;
    private Integer distance;

    public LineStationResponse() {
    }

    public LineStationResponse(StationResponse station, Integer distance) {
        this.id = station.getId();
        this.name = station.getName();
        this.distance = distance;
    }

    public static LineStationResponse of(StationResponse station, LineStation lineStation) {
        return new LineStationResponse(station, lineStation.getDistanceValue());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getDistance() {
        return distance;
    }
}
