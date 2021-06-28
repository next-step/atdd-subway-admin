package nextstep.subway.station.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Stations {
    private List<Station> values = new ArrayList<>();

    public Stations() {

    }
    public Stations(List<Station> values) {
        this.values = values;
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

    public Stream<Station> stream() {
        return this.values.stream();
    }

    public void addAll(Stations stations) {
        this.values.addAll(stations.values);
    }
}
