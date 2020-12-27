package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private final List<Section> sections = new LinkedList<>();

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        Map<Station, Station> sectionElement = sectionElements();
        Station node = getFirstNode(sectionElement);
        stations.add(node);
        while (sectionElement.containsKey(node)) {
            node = sectionElement.get(node);
            stations.add(node);
        }
        return stations;
    }

    public void create(Section section) {
        checkValidation(section);
        this.sections.add(section);
    }

    public void add(Section targetSection) {
        checkValidation(targetSection);
        changeUpStation(targetSection);
        this.sections.add(findAscendIndex(targetSection), targetSection);
    }

    public void checkValidation(Section targetSection) {
        if (this.sections.contains(targetSection) || targetSection.isZeroDistance()) {
            throw new IllegalArgumentException();
        }
    }

    public void update(Section section) {
        if (isChanged(section)) {
            this.sections.stream()
                    .findFirst()
                    .ifPresent(s -> s.update(section));
        }
    }

    public void changeUpStation(Section targetSection) {
        findSameUpStation(targetSection.getUpStation())
                .ifPresent(base -> {
                    base.changeUpStation(targetSection.getDownStation());
                    base.changeDistance(targetSection.getDistance());
                });
    }

    private Optional<Section> findSameUpStation(Station targetStation) {
        return this.sections.stream()
                .filter(section -> section.isSameUpStation(targetStation))
                .findFirst();
    }

    private Optional<Section> findSameDownStation(Station targetStation) {
        return this.sections.stream()
                .filter(base -> base.isSameDownStation(targetStation))
                .findFirst();
    }

    private boolean isChanged(Section section) {
        return !this.sections.stream()
                .allMatch(s -> s.equals(section));
    }

    private Integer findAscendIndex(Section targetSection) {
        return findSameUpStation(targetSection.getDownStation())
                .map(this.sections::indexOf)
                .orElseGet(() -> findDescendIndex(targetSection));
    }

    private Integer findDescendIndex(Section targetSection) {
        return findSameDownStation(targetSection.getUpStation())
                .map(base -> this.sections.indexOf(base) + 1)
                .orElseThrow(IllegalArgumentException::new);
    }

    private Map<Station, Station> sectionElements() {
        return this.sections.stream()
                .collect(Collectors.toMap(
                        Section::getUpStation,
                        Section::getDownStation
                ));
    }

    private Station getFirstNode(Map<Station, Station> stationStationMap) {
        return stationStationMap.keySet()
                .stream()
                .filter(value -> !stationStationMap.containsValue(value))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
