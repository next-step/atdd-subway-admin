package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        addSection(section);
    }

    public void add(Section targetSection) {
        changeBetweenSection(targetSection);
        checkAddValidation(targetSection);

        addSection(targetSection);
    }

    public void update(Section section) {
        if (isChanged(section)) {
            this.sections.stream()
                    .findFirst()
                    .ifPresent(s -> s.update(section));
        }
    }

    private void addSection(Section section) {
        if (section.isZeroDistance()) {
            throw new IllegalArgumentException();
        }
        this.sections.add(section);
    }

    private void checkAddValidation(Section targetSection) {
        if (this.sections.contains(targetSection)) {
            throw new IllegalArgumentException();
        }

        if (checkConnectableSection(targetSection)) {
            throw new IllegalArgumentException();
        }
    }

    private boolean checkConnectableSection(Section targetSection) {
        return this.sections.stream()
                .noneMatch(section -> section.isConnectable(targetSection));
    }

    private void changeBetweenSection(Section targetSection) {
        this.sections.stream()
                .filter(base -> base.isSameUpStation(targetSection))
                .findFirst()
                .ifPresent(base -> base.switchUpStationAndDistance(targetSection));
    }

    private boolean isChanged(Section section) {
        return !this.sections.stream()
                .allMatch(base -> base.equals(section));
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
        Iterator<Section> removeSections = getConnectedSectionsByStation(station);
        Section removeSection = removeSections.next();
        if (removeSections.hasNext()) {
            removeSections.next().merge(removeSection, station);
        }
        this.sections.remove(removeSection);
    }

    private Iterator<Section> getConnectedSectionsByStation(Station station) {
        return Optional.of(this.sections.stream()
                .filter(section -> section.containStation(station))
                .collect(Collectors.toList()).iterator())
                .orElseThrow(IllegalArgumentException::new);
    }

    public boolean isRemovable() {
        return this.sections.size() <= 1;
    }
}
