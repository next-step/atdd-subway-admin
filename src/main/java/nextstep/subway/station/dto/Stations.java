package nextstep.subway.station.dto;

import java.util.Collections;
import java.util.Set;

public class Stations {
    private final Set<StationResponse> stations;

    public Stations(final Set<StationResponse> stations) {
        this.stations = Collections.unmodifiableSet(stations);
    }

    public Set<StationResponse> getStations() {
        return stations;
    }

    @Override
    public String toString() {
        return "Stations{" +
                "stations=" + stations +
                '}';
    }

}
