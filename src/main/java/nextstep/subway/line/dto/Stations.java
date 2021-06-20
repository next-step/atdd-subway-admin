package nextstep.subway.line.dto;

import nextstep.subway.station.domain.Station;

import java.util.*;

public class Stations {
    private Set<Station> stations = new LinkedHashSet<>();

    public Stations() {
    }

    public List<Station> values() {
        return new ArrayList<>(stations);
    }

    public void add(Station upStation) {
        stations.add(upStation);
    }

    public boolean contains(Station station){
        return stations.contains(station);
    }
}
