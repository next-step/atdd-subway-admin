package nextstep.subway.line.dto;


import nextstep.subway.line.domain.LineStation;
import nextstep.subway.station.dto.StationResponse;

public class LineStationResponse {

    private StationResponse station;
    private Integer distance;
    private Integer duration;

    public LineStationResponse() {
    }

    public LineStationResponse(StationResponse station, Integer distance,
        Integer duration) {
        this.station = station;
        this.distance = distance;
        this.duration = duration;
    }

    public static LineStationResponse of(LineStation it, StationResponse station) {
        return new LineStationResponse(station, it.getDistance(), it.getDuration());
    }

    public StationResponse getStation() {
        return station;
    }

    public Integer getDistance() {
        return distance;
    }

    public Integer getDuration() {
        return duration;
    }
}
