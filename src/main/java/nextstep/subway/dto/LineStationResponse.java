package nextstep.subway.dto;

import nextstep.subway.domain.LineStation;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LineStationResponse {
    private Long id;
    private StationResponse upStation;
    private StationResponse downStation;
    private Long distance;

    public LineStationResponse() {
    }

    private LineStationResponse(Long id, StationResponse upStation, StationResponse downStation, Long distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static LineStationResponse of(LineStation lineStation) {
        return new LineStationResponse(lineStation.getId(), StationResponse.of(lineStation.getUpStation()), StationResponse.of(lineStation.getDownStation()), lineStation.getDistance());
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

    public List<StationResponse> inclueStations() {
        return Arrays.asList(upStation, downStation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineStationResponse that = (LineStationResponse) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
