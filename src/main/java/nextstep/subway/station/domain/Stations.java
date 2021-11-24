package nextstep.subway.station.domain;

import java.util.Collections;
import java.util.List;

public class Stations {

    private List<Station> stations;

    public Stations() {
    }

    private Stations(final List<Station> stations) {
        this.stations = stations;
    }

    public static Stations of(final List<Station> stations) {
        return new Stations(stations);
    }

    public List<Station> toList() {
        return Collections.unmodifiableList(stations);
    }
}
