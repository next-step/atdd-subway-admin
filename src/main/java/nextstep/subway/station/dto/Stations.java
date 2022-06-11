package nextstep.subway.station.dto;

import nextstep.subway.station.domain.Station;

import java.util.*;

public class Stations {
    private final List<StationResponse> stations;

    public Stations(final List<StationResponse> stations) {
        this.stations = Collections.unmodifiableList(stations);
    }

    public static Stations of(final Station upStation, final Station downStation) {
        return new Stations(Arrays.asList(StationResponse.of(upStation),
                StationResponse.of(downStation)));
    }

    public List<StationResponse> getStations() {
        return stations;
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
