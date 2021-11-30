package nextstep.subway.section.domain;

import nextstep.subway.exception.InputDataErrorCode;
import nextstep.subway.exception.InputDataErrorException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
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
        Station firstStation = findFirstStation();
        Optional<Section> foundSection = findFirstSection(firstStation);

        while (foundSection.isPresent()) {
            orderedSections.add(foundSection.get());
            foundSection = findNextSection(foundSection.get());
        }

        return orderedSections;
    }

    public List<Station> getOrderedStation() {
        return getOrderedSection().stream()
                .map(Section::getStations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private Optional<Section> findNextSection(Section section) {
        return this.sections.stream()
                .filter(it -> it.getUpStation() == section.getDownStation())
                .findFirst();
    }

    private Optional<Section> findFirstSection(Station firstStation) {
        return this.sections.stream()
                .filter(it -> it.getUpStation() == firstStation)
                .findFirst();
    }

    private Station findFirstStation() {
        List<Station> upStations = getUpStations();
        List<Station> downStations = getDownStations();

        return upStations.stream()
                .filter(it -> !hasStation(it, downStations))
                .findFirst()
                .orElseThrow(
                        ()-> new InputDataErrorException(InputDataErrorCode.THERE_IS_NOT_SEARCHED_STATION)
                );
    }

    private boolean hasStation(Station station, List<Station> downStations) {
        return downStations.contains(station);
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

    public void updateSection(Section newSection) {
        checkValidSection(newSection);
        addSection(newSection);
    }

    public void addSection(Section newSection) {
        for (Section section : sections) {
            section.addInnerSection(newSection);
        }
        sections.add(newSection);
    }

    private void checkValidSection(Section section) {
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

    private boolean hasBothStationInLine(Section section) {
        List<Station> stations = this.getStations();
        return stations.stream()
                .allMatch(it -> isSameSection(section, it));
    }

    private boolean isFindNoOneStationInLine(Section section) {
        List<Station> stations = this.getStations();
        return stations.stream()
                .noneMatch(it -> isSameSection(section, it));
    }

    private boolean isSameSection(Section section, Station it) {
        return it.equals(section.getUpStation()) || it.equals(section.getDownStation());
    }

    private boolean isExist(Section section) {
        return this.sections.stream()
                .anyMatch(it -> isSameSection(section, it));
    }

    private boolean isSameSection(Section section, Section it) {
        return it.equals(section);
    }

    public List<Section> getSections() {
        return sections;
    }
}
