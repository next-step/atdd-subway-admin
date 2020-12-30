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
        return Collections.unmodifiableList(
                Optional.of(sectionElements())
                .filter(sectionElements -> !sectionElements.isEmpty())
                .map(this::convertElementsToStations)
                .orElse(new ArrayList<>()));
    }

    private Map<Station, Station> sectionElements() {
        return this.sections.stream()
                .collect(Collectors.toMap(
                        Section::getUpStation,
                        Section::getDownStation
                ));
    }

    private List<Station> convertElementsToStations(Map<Station, Station> sectionElements) {
        List<Station> stations = new ArrayList<>();
        Station element = getFirstElement(sectionElements);
        stations.add(element);
        while (sectionElements.containsKey(element)) {
            element = sectionElements.get(element);
            stations.add(element);
        }
        return stations;
    }

    private Station getFirstElement(Map<Station, Station> stationStationMap) {
        return stationStationMap.keySet()
                .stream()
                .filter(value -> !stationStationMap.containsValue(value))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public void create(Section targetSection) {
        addSection(targetSection);
    }

    public void add(Section targetSection) {
        changeBetweenSection(targetSection);
        checkAddValidation(targetSection);

        addSection(targetSection);
    }

    public void update(Section targetSection) {
        if (isChanged(targetSection)) {
            this.sections.stream()
                    .findFirst()
                    .ifPresent(section -> section.update(targetSection));
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
                .filter(section -> section.isSameUpStation(targetSection))
                .findFirst()
                .ifPresent(section -> section.switchUpStationAndDistance(targetSection));
    }

    private boolean isChanged(Section targetSection) {
        return !this.sections.stream()
                .allMatch(section -> section.equals(targetSection));
    }

    public void remove(Station targetStation) {
        Iterator<Section> removeSections = getConnectedSectionsByStation(targetStation);
        Section removeSection = removeSections.next();
        if (removeSections.hasNext()) {
            removeSections.next().merge(removeSection, targetStation);
        }
        this.sections.remove(removeSection);
    }

    private Iterator<Section> getConnectedSectionsByStation(Station targetStation) {
        return Optional.of(this.sections.stream()
                .filter(section -> section.containStation(targetStation))
                .collect(Collectors.toList()).iterator())
                .orElseThrow(IllegalArgumentException::new);
    }

    public boolean isRemovable() {
        return this.sections.size() <= 1;
    }
}
