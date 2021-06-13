package nextstep.subway.line.domain;

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nextstep.subway.line.exception.TerminusNotFoundException;
import nextstep.subway.station.domain.Station;

public class Relationship {

    private final Map<Station, Station> relations;

    public static Relationship of(List<Section> sections) {
        return new Relationship(sections.stream()
            .collect(toMap(Section::upStation, Section::downStation)));
    }

    public Relationship(Map<Station, Station> relations) {
        this.relations = relations;
    }

    public List<Station> getSortedStations() {
        List<Station> stations = new ArrayList<>();

        Station station = upTerminus();
        while (relations.containsKey(station)) {
            stations.add(station);
            station = relations.get(station);
        }

        stations.add(station);

        return unmodifiableList(stations);
    }

    public Station upTerminus() {
        return relations.keySet()
            .stream()
            .filter(s -> !relations.containsValue(s))
            .findAny()
            .orElseThrow(() -> new TerminusNotFoundException("출발역이 존재하지 않습니다."));
    }

    public Station downTerminus() {
        return relations.values()
            .stream()
            .filter(s -> !relations.containsKey(s))
            .findAny()
            .orElseThrow(() -> new TerminusNotFoundException("종착역이 존재하지 않습니다."));
    }
}
