package nextstep.subway.station.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Stations {
    private final List<Station> stations;

    public Stations(List<Station> stations) {
        this.stations = stations;
    }

    public boolean isIn(Station station) {
        return stations.contains(station);
    }

    public List<Station> getStations() {
        return new ArrayList<>(stations);
    }

    public boolean isMatch(Station nonPersistStation) {
        return stations.stream().anyMatch(nonPersistStation::equals);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stations stations1 = (Stations) o;
        return Objects.equals(stations, stations1.stations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stations);
    }
}
