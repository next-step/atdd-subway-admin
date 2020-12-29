package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
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
        return Collections.unmodifiableList(stations);
    }

    public void create(Section section) {
        if (section.isZeroDistance()) {
            throw new IllegalArgumentException();
        }
        this.sections.add(section);
    }

    public void add(Section targetSection) {
        if (targetSection.isZeroDistance()) {
            throw new IllegalArgumentException();
        }

        addSection(targetSection);
    }

    public void addSection(Section targetSection) {
        changeBetweenSection(targetSection);
        //둘다 포함되어있는지 확인
        if (this.sections.contains(targetSection)) {
            throw new IllegalArgumentException();
        }

        // 연결 가능한 노드가 있는지 확인
        if (checkConnectableSection(targetSection)) {
            throw new IllegalArgumentException();
        }

        this.sections.add(targetSection);
    }

    private boolean checkConnectableSection(Section targetSection) {
        return !findSameUpStation(targetSection.getDownStation()).isPresent() && !findSameDownStation(targetSection.getUpStation()).isPresent();
    }

    public void update(Section section) {
        if (isChanged(section)) {
            this.sections.stream()
                    .findFirst()
                    .ifPresent(s -> s.update(section));
        }
    }

    public void changeBetweenSection(Section targetSection) {
        findSameUpStation(targetSection.getUpStation())
                .ifPresent(base -> {
                    base.changeUpStation(targetSection.getDownStation());
                    base.changeDistance(targetSection.getDistance());
                });
    }

    public Optional<Section> findSameUpStation(Station targetStation) {
        return this.sections.stream()
                .filter(section -> section.isSameUpStation(targetStation))
                .findFirst();
    }

    public Optional<Section> findSameDownStation(Station targetStation) {
        return this.sections.stream()
                .filter(base -> base.isSameDownStation(targetStation))
                .findFirst();
    }

    private boolean isChanged(Section section) {
        return !this.sections.stream()
                .allMatch(s -> s.equals(section));
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

    public void remove(Station station) {
        Queue<Section> containsSections = getContainsSections(station);
        Section deleteTarget = containsSections.remove();
        if (!containsSections.isEmpty()) {
            containsSections.remove().merge(deleteTarget);
        }
        this.sections.remove(deleteTarget);
    }

    private Queue<Section> getContainsSections(Station station) {
        Queue<Section> containsSections = new LinkedList<>();
        findSameUpStation(station)
                .ifPresent(containsSections::add);
        findSameDownStation(station)
                .ifPresent(containsSections::add);
        if (containsSections.isEmpty() || this.sections.size() <= 1) {
            throw new IllegalArgumentException();
        }
        return containsSections;
    }
}
