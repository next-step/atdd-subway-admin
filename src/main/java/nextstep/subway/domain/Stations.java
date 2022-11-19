package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Stations {
    private final List<Station> stations;

    private Stations(List<Station> stations) {
        this.stations = stations;
    }

    public static Stations of(List<Station> stations) {
        return new Stations(stations);
    }

    public static Stations empty() {
        return new Stations(new ArrayList<>());
    }

    public boolean isEmpty() {
        return this.stations.isEmpty();
    }

    public boolean contains(Station station) {
        return this.stations.contains(station);
    }

    public boolean notContains(Station station) {
        return !this.stations.contains(station);
    }

    public boolean containsAll(Stations stations) {
        return this.stations.containsAll(stations.stations);
    }

    public boolean noneMatch(Stations stations) {
        return this.stations.stream().noneMatch(stations::contains);
    }

    public Stations concatDistinct(Stations other) {
        return Stations.of(
            Stream.concat(this.stations.stream(), other.stations.stream())
                .distinct()
                .collect(Collectors.toList())
        );
    }

    public List<Station> getList() {
        return Collections.unmodifiableList(stations);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Stations stations1 = (Stations)o;
        return Objects.equals(stations, stations1.stations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stations);
    }
}
