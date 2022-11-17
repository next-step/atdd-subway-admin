package nextstep.subway.domain;

import nextstep.subway.exception.SectionsException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.exception.SectionsExceptionMessage.*;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
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
        if (isContainsAllStation(newSection)) {
            throw new SectionsException(ALREADY_CONTAINS_SECTION.getMessage());
        }
        if (isNotContainsAnyStation(newSection)) {
            throw new SectionsException(NOT_CONSTAINS_ANY_SECTION.getMessage());
        }
    }

    private void modifyUpStation(Section newSection) {
        Section findSection = sections.stream().filter(section -> section.isSameUpStation(newSection))
                .findFirst().orElse(null);
        if (findSection == null) {
            return;
        }
        validationDistance(findSection, newSection);
        findSection.modifyUpStation(newSection);
    }

    private void modifyDownStation(Section newSection) {
        Section findSection = sections.stream().filter(section -> section.isSameDownStation(newSection))
                .findFirst().orElse(null);
        if (findSection == null) {
            return;
        }
        validationDistance(findSection, newSection);
        findSection.modifyDownStation(newSection);
    }

    public List<Station> getStations() {
        return sections.stream()
                .flatMap(section -> section.getStations().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    private boolean isContainsAllStation(Section newSection) {
        boolean isSameSection = sections.stream().anyMatch(section -> section.isSameSection(newSection));
        boolean isContainsAllStation = newSection.isComponentAllOfStations(getStations());
        return isSameSection || isContainsAllStation;
    }

    private boolean isNotContainsAnyStation(Section newSection) {
        return !sections.isEmpty() && !newSection.isComponentAnyOfStations(getStations());
    }
}
