package nextstep.subway.line.domain;

import nextstep.subway.common.exception.InvalidDuplicatedSection;
import nextstep.subway.common.exception.NotContainsStationException;
import nextstep.subway.common.exception.NotFoundStationException;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
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
        validateSection(upStation, downStation);

        changeUpStationIfExist(upStation, downStation, distance);
        changeDownStationIfExist(downStation, upStation, distance);

        addToSections(line, upStation, downStation, distance);
    }

    private void validateSection(Station upStation, Station downStation) {
        validateDuplicateSection(upStation, downStation);
        notContainsStationException(upStation, downStation);
    }

    private void changeUpStationIfExist(Station station, Station changeStation, Distance distance) {
        sections.stream()
                .filter(section -> section.isSameUpStation(station))
                .findAny()
                .ifPresent(section -> section.changeUpStation(changeStation, distance));
    }

    private void changeDownStationIfExist(Station station, Station changeStation, Distance distance) {
        sections.stream()
                .filter(section -> section.isSameDownStation(station))
                .findAny()
                .ifPresent(section -> section.changeDownStation(changeStation, distance));
    }

    private void validateDuplicateSection(Station upStation, Station downStation) {
        if (containsUpStation(upStation) && containsDownStation(downStation)) {
            throw new InvalidDuplicatedSection(upStation.getName(), downStation.getName());
        }
    }

    private void notContainsStationException(Station upStation, Station downStation) {
        if (!containsUpStation(upStation) &&
            !containsUpStation(downStation) &&
            !containsDownStation(upStation) &&
            !containsDownStation(downStation)) {
            throw new NotContainsStationException(upStation.getName(), downStation.getName());
        }
    }

    private boolean containsUpStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isSameUpStation(station));
    }

    private boolean containsDownStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isSameDownStation(station));
    }

    public List<Station> getSortedStations() {
        if (sections.isEmpty()) {
            return new ArrayList<>();
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

}
