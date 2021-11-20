package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import java.util.List;

public class Stations {
    private final List<Station> values;

    private Stations(List<Station> stations) {
        this.values = stations;
    }

    public static Stations of(List<Station> stations) {
        return new Stations(stations);
    }

    public List<Station> getValues() {
        return values;
    }
}
