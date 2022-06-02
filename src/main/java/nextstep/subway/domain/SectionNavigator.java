package nextstep.subway.domain;

import java.util.*;

import static java.util.Collections.emptyList;

public class SectionNavigator {
    private final Map<Station, Station> sectionMap;

    public SectionNavigator(List<Section> sections) {
        sectionMap = sections.stream()
                .collect(HashMap::new,
                        (map, section) -> map.put(section.getUpStation(), section.getDownStation()),
                        HashMap::putAll);
    }

    public List<Station> orderedStations() {
        if (sectionMap.isEmpty()) {
            return emptyList();
        }
        List<Station> stations = new ArrayList<>();
        stations.add(beginStation(sectionMap));
        Station upStation = stations.get(0);
        while (sectionMap.containsKey(upStation)) {
            Station downStation = sectionMap.get(upStation);
            stations.add(downStation);
            upStation = downStation;
        }
        return stations;
    }

    private Station beginStation(Map<Station, Station> sectionMap) {
        Set<Station> upStations = sectionMap.keySet();
        Set<Station> downStations = new HashSet<>(sectionMap.values());
        return upStations.stream()
                .filter(station -> !downStations.contains(station))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }
}
