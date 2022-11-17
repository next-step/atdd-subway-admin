package nextstep.subway.domain;

import nextstep.subway.exception.SectionsException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.exception.SectionsExceptionMessage.*;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {

    }

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
        if (newSection == null) {
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

    private boolean isContainsAllStation(Section newSection) {
        return newSection.isComponentAllOfStations(getStations());
    }

    private boolean isNotContainsAnyStation(Section newSection) {
        return !sections.isEmpty() && !newSection.isComponentAnyOfStations(getStations());
    }
}
