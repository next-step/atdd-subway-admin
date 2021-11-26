package nextstep.subway.line.dto;


import nextstep.subway.line.domain.LineStation;
import nextstep.subway.station.dto.StationResponse;

public class LineStationResponse {

    private StationResponse station;
    private Integer distance;

    public LineStationResponse() {
    }

    public LineStationResponse(StationResponse station, Integer distance) {
        this.station = station;
        this.distance = distance;
    }

    public static LineStationResponse of(LineStation it, StationResponse station) {
        return new LineStationResponse(station, it.getDistance());
    }

    public StationResponse getStation() {
        return station;
    }

    public Integer getDistance() {
        return distance;
    }
}
