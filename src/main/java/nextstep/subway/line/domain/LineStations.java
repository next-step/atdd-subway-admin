package nextstep.subway.line.domain;

import java.util.List;
import java.util.Objects;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.StreamUtils;

public class LineStations {

    private final List<Station> stations;

    private LineStations(List<Station> stations) {
        this.stations = stations;
    }

    public static LineStations from(List<Station> stations) {
        return new LineStations(stations);
    }

    public List<StationResponse> convertToStationResponse() {
        return StreamUtils.mapToList(this.stations, StationResponse::of);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LineStations that = (LineStations) o;
        return Objects.equals(stations, that.stations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stations);
    }
}
