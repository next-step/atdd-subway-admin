package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {
    public static final String HAS_UP_AND_DOWN_STATION_MSG = "상행역과 하행역이 이미 다른 구간에 동시에 등록되어 있습니다.";
    public static final String HAS_NOT_UP_AND_DOWN_STATION_MSG = "새로운 구간에 상행역과 하행역 중 하나는 포함되어야 합니다.";

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Station> getStationsSorted() {
        List<Station> stations = new ArrayList<>();

        Station rootStation = getRootStation();
        stations.add(rootStation);
        Station nextStation = getNextStation(rootStation);

        while (Objects.nonNull(nextStation)) {
            stations.add(nextStation);
            nextStation = getNextStation(nextStation);
        }

        return stations;
    }

    private Station getNextStation(Station downStation) {
        Section nextSection = sections.stream()
                .filter(section -> section.getEqualsUpStation(downStation))
                .findFirst()
                .orElse(null);

        if (Objects.nonNull(nextSection)) {
            return nextSection.getDownStation();
        }

        return null;
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        addSectionValid(section);
        updateExistSection(section);
        sections.add(section);
    }

    private void addSectionValid(Section section) {
        if (hasUpStationAndDownStation(section)) {
            throw new IllegalArgumentException(HAS_UP_AND_DOWN_STATION_MSG);
        }

        if (hasNotUpStationAndDownStation(section)) {
            throw new IllegalArgumentException(HAS_NOT_UP_AND_DOWN_STATION_MSG);
        }
    }

    private boolean hasUpStationAndDownStation(Section newSection) {
        return newSection.isUpAndDownStationContains(includeStations());
    }

    private boolean hasNotUpStationAndDownStation(Section newSection) {
        return newSection.isUpAndDownStationNotContains(includeStations());
    }

    public Set<Station> includeStations() {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .collect(Collectors.toSet());
    }
    
    private void updateExistSection(Section newSection) {
        updateEqualsUpStation(newSection);
        updateEqualsDownStation(newSection);
    }

    private void updateEqualsUpStation(Section newSection) {
        sections.stream()
                .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
                .findFirst()
                .ifPresent(section -> section.updateUpSection(newSection));
    }

    private void updateEqualsDownStation(Section newSection) {
        sections.stream()
                .filter(section -> section.getDownStation().equals(newSection.getDownStation()))
                .findFirst()
                .ifPresent(section -> section.updateDownSection(newSection));
    }

    private Station getRootStation() {
        return getUpStations().stream()
                .filter(station -> !getDownStations().contains(station))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    private Station getLastStation() {
        return getDownStations().stream()
                .filter(station -> !getUpStations().contains(station))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    private Section getRootSection() {
        final Station rootStation = getRootStation();
        return sections.stream()
                        .filter(section -> section.getUpStation() == rootStation)
                        .findFirst()
                        .orElseThrow(NoSuchElementException::new);
    }

    private Section getLastSection() {
        final Station lastStation = getLastStation();
        return sections.stream()
                .filter(section -> section.getDownStation() == lastStation)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    private Set<Station> getUpStations() {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation()))
                .collect(Collectors.toSet());
    }

    private Set<Station> getDownStations() {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getDownStation()))
                .collect(Collectors.toSet());
    }

    public void removeSection(Station station) {
        if(!includeStations().contains(station)) {
            throw new IllegalArgumentException();
        }

        if (sections.size() == 1) {
            throw new IllegalArgumentException();
        }

        if (station == getRootStation()) {
            removeRootSection();
            return;
        }

        if (station == getLastStation()) {
            removeLastSection();
            return;
        }

        Section upSection = findSectionByDownStation(station);
        Section downSection = findSectionByUpStation(station);

        upSection.removeUpdateSection(downSection);
        sections.remove(downSection);
    }

    private void removeRootSection() {
        sections.remove(getRootSection());
    }

    private void removeLastSection() {
        sections.remove(getLastSection());
    }

    private Section findSectionByUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.getEqualsUpStation(station))
                .findFirst()
                .get();
    }

    private Section findSectionByDownStation(Station station) {
        return sections.stream()
                .filter(section -> section.getEqualsDownStation(station))
                .findFirst()
                .get();
    }

    public List<Section> getSections() {
        return sections;
    }
}
