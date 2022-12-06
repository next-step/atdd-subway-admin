package nextstep.subway.domain;

import nextstep.subway.constants.ErrorCode;
import org.springframework.data.annotation.ReadOnlyProperty;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {

    private final static int ZERO = 0;
    private final static int SIZE_OF_DELETE_ABLE_MIN_SECTIONS = 2;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    @ReadOnlyProperty
    private final List<Section> sections;

    public Sections() {
        sections = new ArrayList<>();
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section section) {
        checkValidation(section);
        if (isSameUpStation(section)) {
            updateStationWhenUpStationSame(section);
        }
        if (isSameDownStation(section)) {
            updateStationWhenDownStationSame(section);
        }
        sections.add(section);
    }

    private boolean hasEndPointIssue(Section newSection) {
        Section firstSection = findFirstSection();
        Section lastSection = findLastSection();
        return firstSection.isEqualUpStationNewSectionDownStation(newSection)
                && lastSection.isEqualDownStationNewSectionUpStation(newSection);
    }


    private void updateStationWhenDownStationSame(Section section) {
        sections.stream()
                .filter(section::isSameDownStation)
                .findFirst()
                .ifPresent(findSection -> findSection.updateAndCreateTwiceSectionWhenDownStationSame(section));
    }

    private void updateStationWhenUpStationSame(Section section) {
        sections.stream()
                .filter(section::isSameUpStation)
                .findFirst()
                .ifPresent(findSection -> findSection.updateAndCreateTwiceSectionWhenUpStationSame(section));
    }

    private boolean isSameUpStation(Section section) {
        return sections.stream()
                .filter(eachSection -> eachSection.isSameUpStation(section))
                .findFirst().isPresent();
    }

    private boolean isSameDownStation(Section section) {
        return sections.stream()
                .filter(eachSection -> eachSection.isSameDownStation(section))
                .findFirst().isPresent();
    }

    private void checkValidation(Section section) {
        checkDuplicatedBothStation(section);
        checkDuplicatedSection(section);
        checkNoMatchSection(section);
        checkAddEndPointIssue(section);
    }

    private void checkDuplicatedBothStation(Section section) {
        if (isSectionsSizeZero()) {
            return;
        }
        List<Station> allStation = getSortedStations();
        if (allStation.contains(section.getUpStation()) && allStation.contains(section.getDownStation())) {
            throw new IllegalArgumentException(ErrorCode.BOTH_STATION_ALREADY_EXIST_EXCEPTION.getErrorMessage());
        }
    }

    private void checkDuplicatedSection(Section section) {
        if (sections.stream().anyMatch(eachSection -> eachSection.equals(section))) {
            throw new IllegalArgumentException(ErrorCode.NO_SAME_SECTION_EXCEPTION.getErrorMessage());
        }
    }

    private void checkNoMatchSection(Section section) {
        if (isSectionsNotEmpty() && isNoMatchStation(section)) {
            throw new IllegalArgumentException(ErrorCode.NO_MATCH_STATION_EXCEPTION.getErrorMessage());
        }
    }

    private void checkAddEndPointIssue(Section section) {
        if (isSectionsNotEmpty() && !isSameUpStation(section) && !isSameDownStation(section) && hasEndPointIssue(section)) {
            throw new IllegalArgumentException(ErrorCode.ADD_END_POINT_ISSUE.getErrorMessage());
        }
    }

    private boolean isNoMatchStation(Section newSection) {
        return sections.stream()
                .map(Section::toStations)
                .flatMap(Collection::stream)
                .distinct()
                .noneMatch(station -> newSection.isSameDownStation(station) ||
                        newSection.isSameUpStation(station));
    }

    public List<Section> asList() {
        return Collections.unmodifiableList(sections);
    }

    public List<Station> getSortedStations() {
        List<Station> sortedStations = new ArrayList<>();
        Section firstSection = findFirstSection();
        firstSection.addStations(sortedStations);
        addNextStation(sortedStations, firstSection);
        return sortedStations;
    }

    private void addNextStation(List<Station> stations, Section previousSection) {
        Optional<Section> nextSection = findNextSection(previousSection);
        while (nextSection.isPresent()) {
            Section currentSection = nextSection.get();
            currentSection.addNextStation(stations);
            nextSection = findNextSection(currentSection);
        }
    }

    private Optional<Section> findNextSection(Section previousSection) {
        return sections.stream()
                .filter(section -> section.isEqualUpStationNewSectionDownStation(previousSection))
                .findFirst();
    }

    private Section findLastSection() {
        Section lastSection = sections.get(0);
        Optional<Section> nextSection = findNextSection(lastSection);
        while (nextSection.isPresent()) {
            lastSection = nextSection.get();
            nextSection = findNextSection(lastSection);
        }
        return lastSection;
    }

    private Section findFirstSection() {
        Section firstSection = sections.get(0);
        Optional<Section> previousSection = findPreviousSection(firstSection);
        while (previousSection.isPresent()) {
            firstSection = previousSection.get();
            previousSection = findPreviousSection(firstSection);
        }
        return firstSection;
    }

    private Optional<Section> findPreviousSection(Section currentSection) {
        return sections.stream()
                .filter(section -> section.isEqualDownStationNewSectionUpStation(currentSection))
                .findFirst();
    }

    private boolean isSectionsNotEmpty() {
        return this.sections.size() > ZERO;
    }

    private boolean isSectionsSizeZero() {
        return this.sections.size() == ZERO;
    }

    public boolean isDeleteUnable() {
        return this.sections.size() < SIZE_OF_DELETE_ABLE_MIN_SECTIONS;
    }

    public int size() {
        return this.sections.size();
    }

    public boolean hasNotStation(Station station) {
        return this.sections.stream()
                .map(Section::toStations)
                .flatMap(Collection::stream)
                .distinct()
                .noneMatch(eachStation -> eachStation.equals(station));
    }
}
