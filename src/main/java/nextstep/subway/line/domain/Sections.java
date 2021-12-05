package nextstep.subway.line.domain;

import nextstep.subway.common.exception.EmptySectionException;
import nextstep.subway.common.exception.InvalidDuplicatedSection;
import nextstep.subway.common.exception.MinimumRemovableSectionSizeException;
import nextstep.subway.common.exception.NotContainsStationException;
import nextstep.subway.common.exception.NotFoundStationException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    public static final int MINIMUM_REMOVABLE_SECTION_SIZE = 2;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addToSections(Line line, Station upStation, Station downStation, Distance distance) {
        sections.add(new Section(line, upStation, downStation, distance));
    }

    public void addSection(Line line, Station upStation, Station downStation, Distance distance) {
        validateAddSection(upStation, downStation);

        changeUpStationIfExist(upStation, downStation, distance);
        changeDownStationIfExist(downStation, upStation, distance);

        addToSections(line, upStation, downStation, distance);
    }

    private void changeUpStationIfExist(Station station, Station changeStation, Distance distance) {
        sections.stream()
                .filter(section -> section.isSameUpStation(station))
                .findAny()
                .ifPresent(section -> section.changeUpStation(changeStation, section.subtractDistance(distance)));
    }

    private void changeDownStationIfExist(Station station, Station changeStation, Distance distance) {
        sections.stream()
                .filter(section -> section.isSameDownStation(station))
                .findAny()
                .ifPresent(section -> section.changeDownStation(changeStation, section.subtractDistance(distance)));
    }

    public void removeSection(Station station) {
        validateRemoveSection(station);

        updateIfMiddleSection(station);

        removeSectionIfStationExist(station);
    }

    private void updateIfMiddleSection(Station station) {
        if (!containsUpStation(station) || !containsDownStation(station)) {
            return;
        }

        Station downStation = getRemovableDownStation(station);
        Distance distance = getRemovableDistance(station);
        updateMiddleSection(station, downStation, distance);

    }

    private void removeSectionIfStationExist(Station station) {
        removeSectionIfUpStationExist(station);
        removeSectionIfDownStationExist(station);
    }

    private Distance getRemovableDistance(Station station) {
        return sections.stream()
                .filter(section -> section.isSameUpStation(station))
                .findAny()
                .orElseThrow(NotFoundStationException::new)
                .getDistance();
    }

    private void updateMiddleSection(Station station, Station downStation, Distance distance) {
        sections.stream()
                .filter(section -> section.isSameDownStation(station))
                .findAny()
                .ifPresent(section -> section.changeDownStation(downStation, section.addDistance(distance)));
    }

    private Station getRemovableDownStation(Station station) {
        return sections.stream()
                .filter(section -> section.isSameUpStation(station))
                .findAny()
                .orElseThrow(NotFoundStationException::new)
                .getDownStation();
    }

    private void removeSectionIfUpStationExist(Station station) {
        sections.stream()
                .filter(section -> section.isSameUpStation(station))
                .findAny()
                .ifPresent(section -> sections.remove(section));
    }

    private void removeSectionIfDownStationExist(Station station) {
        sections.stream()
                .filter(section -> section.isSameDownStation(station))
                .findAny()
                .ifPresent(section -> sections.remove(section));
    }

    public List<Station> getSortedStations() {
        if (sections.isEmpty()) {
            throw new EmptySectionException();
        }

        return sortStations();
    }

    private List<Station> sortStations() {
        Set<Station> stations = new LinkedHashSet<>();
        Section currentSection = findFirstSection();
        Section endSection = findLastSection();

        while (!currentSection.equals(endSection)) {
            addStations(stations, currentSection);
            currentSection = findNextSection(currentSection);
        }
        addStations(stations, endSection);

        return new ArrayList<>(stations);
    }

    private void addStations(Set<Station> stations, Section currentSection) {
        stations.add(currentSection.getUpStation());
        stations.add(currentSection.getDownStation());
    }

    private Section findFirstSection() {
        Set<Station> downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toCollection(HashSet::new));

        return sections.stream()
                .filter(section -> !downStations.contains(section.getUpStation()))
                .findAny()
                .orElseThrow(NotFoundStationException::new);
    }

    private Section findLastSection() {
        Set<Station> upStations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toCollection(HashSet::new));

        return sections.stream()
                .filter(section -> !upStations.contains(section.getDownStation()))
                .findAny()
                .orElseThrow(NotFoundStationException::new);
    }

    private Section findNextSection(Section currentSection) {
        return sections.stream()
                .filter(section -> section.isSameUpStation(currentSection.getDownStation()))
                .findAny()
                .orElseThrow(NotFoundStationException::new);
    }

    private boolean notContainsStation(Station station) {
        return !containsUpStation(station) && !containsDownStation(station);
    }

    private boolean containsUpStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isSameUpStation(station));
    }

    private boolean containsDownStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isSameDownStation(station));
    }

    private void validateRemoveSection(Station station) {
        validateMinimumRemovableSectionSize();
        validateContainsRemovableStation(station);
    }

    private void validateMinimumRemovableSectionSize() {
        if (sections.size() < MINIMUM_REMOVABLE_SECTION_SIZE) {
            throw new MinimumRemovableSectionSizeException(sections.size());
        }
    }

    private void validateContainsRemovableStation(Station station) {
        if (notContainsStation(station)) {
            throw new NotContainsStationException(station.getName());
        }
    }

    private void validateAddSection(Station upStation, Station downStation) {
        validateDuplicateSection(upStation, downStation);
        validateContainsAddableStation(upStation, downStation);
    }

    private void validateDuplicateSection(Station upStation, Station downStation) {
        if (containsUpStation(upStation) && containsDownStation(downStation)) {
            throw new InvalidDuplicatedSection(upStation.getName(), downStation.getName());
        }
    }

    private void validateContainsAddableStation(Station upStation, Station downStation) {
        if (notContainsStation(upStation) && notContainsStation(downStation)) {
            throw new NotContainsStationException(upStation.getName(), downStation.getName());
        }
    }

}
