package nextstep.subway.line.dto;

import nextstep.subway.station.domain.Station;

import java.util.*;

public class Stations {
    private List<Station> stations = new ArrayList<>();

    public Stations() {
    }

    public List<Station> values() {
        return stations;
    }

    public void add(Station upStation) {
        stations.add(upStation);
    }

    public boolean contains(Station station){
        return stations.contains(station);
    }
}
