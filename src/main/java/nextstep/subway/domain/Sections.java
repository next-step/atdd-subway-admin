package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

import static java.util.Collections.emptyList;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Section> sections = new LinkedList<>();

    public void add(Section section) {
        if (addInitial(section)) {
            return;
        }
        if (exist(section)) {
            throw new IllegalArgumentException();
        }
        if (addToInside(section)) {
            return;
        }
        if (addToOutside(section)) {
            return;
        }
        throw new IllegalArgumentException();
    }

    public List<Station> stations() {
        if (sections.isEmpty()) {
            return emptyList();
        }
        Map<Station, Station> sectionMap = sectionMap();

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

    private Map<Station, Station> sectionMap() {
        return sections.stream()
                .collect(HashMap::new,
                        (map, section) -> map.put(section.getUpStation(), section.getDownStation()),
                        HashMap::putAll);
    }

    private Station beginStation(Map<Station, Station> sectionMap) {
        Set<Station> upStations = sectionMap.keySet();
        Set<Station> downStations = new HashSet<>(sectionMap.values());
        return upStations.stream()
                .filter(station -> !downStations.contains(station))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    private boolean addInitial(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return true;
        }
        return false;
    }

    private boolean exist(Section section) {
        Map<Station, Station> sectionMap = sectionMap();
        Set<Station> stations = new HashSet<>();
        stations.addAll(sectionMap.keySet());
        stations.addAll(sectionMap.values());
        return stations.contains(section.getUpStation()) && stations.contains(section.getDownStation());
    }

    private boolean addToInside(Section newSection) {
        for (Section section : sections) {
            if (section.add(newSection)) {
                sections.add(newSection);
                return true;
            }
        }
        return false;
    }

    private boolean addToOutside(Section newSection) {
        for (Section section : sections) {
            if (section.getUpStation() == newSection.getDownStation() ||
                    section.getDownStation() == newSection.getUpStation()) {
                sections.add(newSection);
                return true;
            }
        }
        return false;
    }
}
