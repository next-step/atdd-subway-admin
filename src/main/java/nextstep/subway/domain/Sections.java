package nextstep.subway.domain;

import nextstep.subway.exception.SectionsException;
import nextstep.subway.exception.StationException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static nextstep.subway.exception.SectionsExceptionMessage.*;
import static nextstep.subway.exception.StationExceptionMessage.NONE_EXISTS_STATION;

@Embeddable
public class Sections {
    private static final int MAX_SECTION_OF_STATION = 2;
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {}

    public void addSection(Section newSection) {
        validationSection(newSection);
        modifyUpStation(newSection);
        modifyDownStation(newSection);
        sections.add(newSection);
    }

    private void validationDistance(Section section, Section newSection) {
        if (section.isShortDistance(newSection)) {
            throw new SectionsException(LONGER_THAN_OHTER.getMessage());
        }
    }

    private void validationSection(Section newSection) {
        if (Objects.isNull(newSection)) {
            throw new NullPointerException(EMPTY_SECTION.getMessage());
        }
        if (isContainsAllStation(newSection)) {
            throw new SectionsException(ALREADY_CONTAINS_SECTION.getMessage());
        }
        if (isNotContainsAnyStation(newSection)) {
            throw new SectionsException(NOT_CONSTAINS_ANY_SECTION.getMessage());
        }
    }

    private void modifyUpStation(Section newSection) {
        sections.stream()
                .filter(section -> section.isSameUpStation(newSection))
                .findFirst()
                .ifPresent(section -> {
                    validationDistance(section, newSection);
                    section.modifyUpStation(newSection);
                });

    }

    private void modifyDownStation(Section newSection) {
        sections.stream()
                .filter(section -> section.isSameDownStation(newSection))
                .findFirst()
                .ifPresent(section -> {
                    validationDistance(section, newSection);
                    section.modifyDownStation(newSection);
                });
    }

    public List<Station> getStations() {
        return sections.stream()
                .flatMap(section -> section.getStations().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public void deleteSection(Line line, Station deleteStation) {
        List<Section> findSections = sections.stream().filter(section -> section.hasStation(deleteStation))
                .collect(Collectors.toList());
        validateSections(findSections, sections);
        if (isSingleSection(findSections)) {
            sections.remove(findSections.get(0));
            return;
        }
        arrageSection(line, findSections, deleteStation);
    }

    private void validateSections(List<Section> findSections, List<Section> sections) {
        validateEmptySection(findSections);
        validateSingleSection(findSections, sections);
        validateMultiSection(findSections);
    }

    private void validateMultiSection(List<Section> findSections) {
        if (findSections.size() > MAX_SECTION_OF_STATION) {
            throw new SectionsException(MULTI_SECTION.getMessage());
        }
    }

    private boolean isContainsAllStation(Section newSection) {
        return newSection.isComponentAllOfStations(getStations());
    }

    private boolean isNotContainsAnyStation(Section newSection) {
        return !sections.isEmpty() && !newSection.isComponentAnyOfStations(getStations());
    }

    private void validateEmptySection(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new SectionsException(NOT_REGISTER_SECTION.getMessage());
        }
    }

    private void validateSingleSection(List<Section> sections, List<Section> findSections) {
        if (isSingleSection(sections) && isSingleSection(findSections)) {
            throw new SectionsException(SINGLE_SECTION.getMessage());
        }
    }

    private boolean isSingleSection(List<Section> findSections) {
        return findSections.size() == 1;
    }

    private void arrageSection(Line line, List<Section> findSections, Station deleteStation) {
        sections.removeAll(findSections);
        Station upStation = getUpStation(findSections, deleteStation);
        Station downStation = getDownStation(findSections, deleteStation);
        long distance = findSections.stream().mapToLong(Section::getDistance).sum();
        sections.add(new Section(line, upStation, downStation, distance));
    }

    private Station getUpStation(List<Section> sections, Station station) {
        return sections.stream()
                .filter(section -> section.isSameDownStation(station))
                .map(section -> section.getUpStation())
                .findFirst()
                .orElseThrow(() -> new StationException(NONE_EXISTS_STATION.getMessage()));
    }

    private Station getDownStation(List<Section> sections, Station station) {
        return sections.stream()
                .filter(section -> section.isSameUpStation(station))
                .map(section -> section.getDownStation())
                .findFirst()
                .orElseThrow(() -> new StationException(NONE_EXISTS_STATION.getMessage()));
    }
}
