package nextstep.subway.line.domain;

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nextstep.subway.line.exception.TerminusNotFoundException;
import nextstep.subway.station.domain.Station;

public class SectionSorter {

    public static List<Station> getStationsInOrder(List<Section> sections) {
        Map<Station, Station> relations = getRelations(sections);

        List<Station> stations = new ArrayList<>();
        Station station = upTerminus(relations);
        while (relations.containsKey(station)) {
            stations.add(station);
            station = relations.get(station);
        }

        stations.add(station);

        return unmodifiableList(stations);
    }

    private static Station upTerminus(Map<Station, Station> relations) {
        return relations.keySet()
            .stream()
            .filter(s -> !relations.containsValue(s))
            .findAny()
            .orElseThrow(() -> new TerminusNotFoundException("출발역이 존재하지 않습니다."));
    }

    private static Map<Station, Station> getRelations(List<Section> sections) {
        return sections.stream()
            .collect(toMap(Section::upStation, Section::downStation));
    }
}
