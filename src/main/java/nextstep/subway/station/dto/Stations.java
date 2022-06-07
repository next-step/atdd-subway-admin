package nextstep.subway.station.dto;

import nextstep.subway.station.domain.Station;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Stations {
    private final List<Station> stations;

    private Stations(final List<Station> stations) {
        this.stations = Collections.unmodifiableList(stations);
    }

    public static Stations of(final List<Station> stations) {
        return new Stations(stations);
    }

    public List<StationResponse> getStationResponse() {
        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Stations{" +
                "stations=" + stations +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Stations stations1 = (Stations) o;
        return Objects.equals(stations, stations1.stations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stations);
    }
}
