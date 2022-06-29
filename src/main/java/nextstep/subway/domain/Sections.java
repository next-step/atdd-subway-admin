package nextstep.subway.domain;

import nextstep.subway.exception.DuplicatedSectionException;
import nextstep.subway.exception.InvalidRemoveSectionException;
import nextstep.subway.exception.InvalidSectionException;
import nextstep.subway.exception.NotFoundSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    private static final int MIN = 1;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections;

    public Sections() {
        this.sections = new ArrayList<>();
    }

    public List<Section> getSections() {
        return sections;
    }

    public void add(Section newSection) {
        if (this.sections.isEmpty()) {
            sections.add(newSection);
            return;
        }

        validateIncludeAnyStation(newSection);
        validateUnique(newSection);
        validateDistance(newSection);
        sections.add(newSection);
    }

    private void validateIncludeAnyStation(Section newSection) {
        boolean contains = findStations().stream().anyMatch(newSection::includeAnySection);
        if (!contains) {
            throw new InvalidSectionException();
        }
    }

    private void validateUnique(Section newSection) {
        if (sections.contains(newSection)) {
            throw new DuplicatedSectionException(newSection);
        }
    }

    public List<Station> findStations() {
        return this.sections.stream()
            .map(Section::getStations)
            .flatMap(List::stream)
            .distinct()
            .collect(Collectors.toList());
    }

    public void validateDistance(Section newSection) {
        Section connectedSection = findConnectedSection(newSection);
        if (connectedSection == null) {
            return;
        }

        connectedSection.validateDistanceAndUpdateSection(newSection);
    }

    private Section findConnectedSection(Section newSection) {
        return this.sections.stream()
            .filter(section -> section.isBetweenStation(newSection))
            .findFirst()
            .orElse(null);
    }

    public void remove(Station station) throws NotFoundSectionException {
        validateStation(station);
        validateMinimumSections();

        Section matchedUpStation = this.sections.stream().filter(section -> station.equals(section.getUpStation())).findFirst().orElse(null);
        Section matchedDownStation = this.sections.stream().filter(section -> station.equals(section.getDownStation())).findFirst().orElse(null);

        if (isUpStationLastStop(matchedDownStation)) {
            this.sections.remove(matchedUpStation);
            return;
        }

        if (isDownStationLastStop(matchedUpStation)) {
            this.sections.remove(matchedDownStation);
            return;
        }

        removeBetweenStation(matchedUpStation, matchedDownStation);
    }

    private void validateStation(Station station) throws NotFoundSectionException {
        boolean contains = this.sections.stream().anyMatch(section -> section.includeAnySection(station));
        if (!contains) {
            throw new NotFoundSectionException();
        }
    }

    private void removeBetweenStation(Section upSection, Section downSection) {
        downSection.merge(upSection);
        this.sections.remove(upSection);
    }

    private void validateMinimumSections() {
        if (this.sections.size() <= MIN) {
            throw new InvalidRemoveSectionException();
        }
    }

    private boolean isDownStationLastStop(Section matchedUpStation) {
        return matchedUpStation == null;
    }

    private boolean isUpStationLastStop(Section matchedDownStation) {
        return matchedDownStation == null;
    }
}
