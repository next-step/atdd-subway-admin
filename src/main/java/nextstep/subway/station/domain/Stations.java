package nextstep.subway.station.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Stations {
    private List<Station> values = new ArrayList<>();

    public Stations() {

    }
    public Stations(List<Station> values) {
        this.values = values;
    }

    public List<Station> get() {
        return values;
    }

    public Station get(int index) {
        return values.get(index);
    }

    public int size() {
        return values.size();
    }

    public int lastIndex() {
        return values.size() - 1;
    }

    public void addAll(Stations stations) {
        this.values.addAll(stations.values);
    }

    public boolean contains(Station station) {
        return values.contains(station);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stations stations = (Stations) o;
        return Objects.equals(values, stations.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}
