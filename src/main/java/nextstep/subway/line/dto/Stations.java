package nextstep.subway.line.dto;

import nextstep.subway.station.domain.Station;

import java.util.*;
import java.util.stream.Collectors;

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


    public boolean containsId(Long stationId) {
        List<Long> ids =  stations.stream().map(Station::getId).collect(Collectors.toList());
        return ids.contains(stationId);
    }
}
