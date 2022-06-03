package nextstep.subway.dto;

import nextstep.subway.domain.LineStation;

public class LineStationResponse {
    private Long id;
    private StationResponse upStation;
    private StationResponse downStation;
    private Long distance;

    public static LineStationResponse of(LineStation lineStation) {
        return new LineStationResponse(lineStation.getId(), StationResponse.of(lineStation.getUpStation()), StationResponse.of(lineStation.getDownStation()), lineStation.getDistance());
    }

    public LineStationResponse() {
    }

    public LineStationResponse(Long id, StationResponse upStation, StationResponse downStation, Long distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public StationResponse getUpStation() {
        return upStation;
    }

    public StationResponse getDownStation() {
        return downStation;
    }

    public Long getDistance() {
        return distance;
    }
}
