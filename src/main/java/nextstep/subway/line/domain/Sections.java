package nextstep.subway.line.domain;

import nextstep.subway.exception.InputDataErrorCode;
import nextstep.subway.exception.InputDataErrorException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Station> getStations() {
        return this.sections.stream()
                .map(Section::getStations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Section> getOrderedSection() {
        List<Section> orderedSections = new ArrayList<>();
        Section foundSection = findFirstSection();

        while (hasSection(foundSection)) {
            orderedSections.add(foundSection);
            foundSection = findNextSection(foundSection);
        }
        return orderedSections;
    }

    public Station findFirstStation() {
        List<Station> upStations = getUpStations();
        List<Station> downStations = getDownStations();

        return upStations.stream()
                .filter(it -> !hasStation(it, downStations))
                .findFirst()
                .orElseThrow(
                        () -> new InputDataErrorException(InputDataErrorCode.THERE_IS_NOT_SEARCHED_STATION)
                );
    }

    public Station findLastStation() {
        List<Station> upStations = getUpStations();
        List<Station> downStations = getDownStations();

        return downStations.stream()
                .filter(it -> !hasStation(it, upStations))
                .findFirst()
                .orElseThrow(
                        () -> new InputDataErrorException(InputDataErrorCode.THERE_IS_NOT_SEARCHED_STATION)
                );
    }

    public List<Station> getOrderedStation() {
        return getOrderedSection().stream()
                .map(Section::getStations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public void removeStation(Station station) {

        List<Section> foundSections = findSections(station);
        checkValidateRemoveSection(foundSections);

        updateSection(station, foundSections);
        sections.remove(findSection(station));
    }

    private void updateSection(Station station, List<Section> foundSections) {
        if (isMiddleRemoval(foundSections)) {
            Section upSection = foundSections.stream()
                    .filter(it -> it.getUpStation() == station)
                    .findFirst()
                    .orElseThrow(() -> new InputDataErrorException(InputDataErrorCode.THERE_IS_NOT_SEARCHED_STATION));

            Section downSection = this.sections.stream()
                    .filter(it -> it.getDownStation() == station)
                    .findFirst()
                    .orElseThrow(() -> new InputDataErrorException(InputDataErrorCode.THERE_IS_NOT_SEARCHED_STATION));

            downSection.removeInnerSection(upSection, downSection);
        }
    }

    private boolean isMiddleRemoval(List<Section> foundSections) {
        return foundSections.size() > 1;
    }

    private void checkValidateRemoveSection(List<Section> foundSections) {
        if (foundSections.isEmpty()) {
            throw new InputDataErrorException(InputDataErrorCode.THEY_ARE_NOT_SEARCHED_STATIONS);
        }
    }

    public void addSection(Section newSection) {
        checkValidAddSection(newSection);
        for (Section section : sections) {
            section.addInnerSection(newSection);
        }
        sections.add(newSection);
    }

    public List<Section> getSections() {
        return sections;
    }

    public boolean hasLastOneSection() {
        return this.sections.size() == 1;
    }

    public boolean contains(Station station) {
        return getStations().contains(station);
    }

    private Section findNextSection(Section section) {
        return this.sections.stream()
                .filter(it -> it.getUpStation() == section.getDownStation())
                .findFirst()
                .orElse(null);
    }

    private boolean hasSection(Section foundSection) {
        return Optional.ofNullable(foundSection).isPresent();
    }

    private Section findFirstSection() {
        Station firstStation = findFirstStation();
        return this.sections.stream()
                .filter(it -> it.getUpStation() == firstStation)
                .findFirst()
                .orElseThrow(
                        () -> new InputDataErrorException(InputDataErrorCode.THERE_IS_NOT_SEARCHED_SECTION)
                );
    }

    private boolean hasStation(Station station, List<Station> stations) {
        return stations.contains(station);
    }

    private List<Station> getDownStations() {
        return this.getSections().stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    private List<Station> getUpStations() {
        return this.getSections().stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    private void checkValidAddSection(Section section) {
        if (checkAddFirstSection()) {
            return;
        }
        if (isExist(section)) {
            throw new InputDataErrorException(InputDataErrorCode.THE_SECTION_ALREADY_EXISTS);
        }
        if (hasBothStationInLine(section)) {
            throw new InputDataErrorException(InputDataErrorCode.THE_STATIONS_ALREADY_EXISTS);
        }
        if (isFindNoOneStationInLine(section)) {
            throw new InputDataErrorException(InputDataErrorCode.THERE_IS_NOT_SEARCHED_STATION);
        }
    }

    private boolean checkAddFirstSection() {
        return this.sections.isEmpty();
    }

    private boolean hasBothStationInLine(Section section) {
        List<Station> stations = this.getStations();
        return stations.stream()
                .allMatch(it -> isSameAnyStationInSection(section, it));
    }

    private boolean isFindNoOneStationInLine(Section section) {
        List<Station> stations = this.getStations();
        return stations.stream()
                .noneMatch(it -> isSameAnyStationInSection(section, it));
    }

    private boolean isExist(Section section) {
        return this.sections.stream()
                .anyMatch(it -> isSameAnyStationInSection(section, it));
    }

    private boolean isSameAnyStationInSection(Section section, Station it) {
        return it.equals(section.getUpStation()) || it.equals(section.getDownStation());
    }

    private boolean isSameAnyStationInSection(Section section, Section it) {
        return it.equals(section);
    }

    private Section findSection(Station station) {
        return this.sections.stream()
                .filter(it -> hasSameStationInSection(station, it))
                .findFirst()
                .orElseThrow(
                        () -> new InputDataErrorException(InputDataErrorCode.THERE_IS_NOT_SEARCHED_SECTION)
                );
    }

    private List<Section> findSections(Station station) {
        return this.sections.stream()
                .filter(it -> hasSameStationInSection(station, it))
                .collect(Collectors.toList());
    }

    private boolean hasSameStationInSection(Station station, Section it) {
        return it.getUpStation().equals(station) || it.getDownStation().equals(station);
    }

}
