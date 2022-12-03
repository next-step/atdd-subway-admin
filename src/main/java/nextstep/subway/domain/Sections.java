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
        boolean hasIssue = false;
        Section firstSection = findFirstSection();
        Section lastSection = findLastSection();
        if (firstSection.isEqualUpStationNewSectionDownStation(newSection)
                && lastSection.isEqualDownStationNewSectionUpStation(newSection)) {
            hasIssue = true;
        }
        return hasIssue;
    }


    private void updateStationWhenDownStationSame(Section section) {
        Optional<Section> findSection = sections.stream().filter(eachSection -> eachSection.isSameDownStationId(section)).findFirst();
        findSection.get().updateAndCreateTwiceSectionWhenDownStationSame(section);
    }

    private void updateStationWhenUpStationSame(Section section) {
        Optional<Section> findSection = sections.stream().filter(eachSection -> eachSection.isSameUpStationId(section)).findFirst();
        findSection.get().updateAndCreateTwiceSectionWhenUpStationSame(section);
    }

    private boolean isSameUpStation(Section section) {
        return sections.stream()
                .filter(eachSection -> eachSection.isSameUpStationId(section))
                .findFirst().isPresent();
    }

    private boolean isSameDownStation(Section section) {
        return sections.stream()
                .filter(eachSection -> eachSection.isSameDownStationId(section))
                .findFirst().isPresent();
    }

    private void checkValidation(Section section) {
        if (sections.stream().anyMatch(s -> s.equals(section))) {
            throw new IllegalArgumentException(ErrorCode.NO_SAME_SECTION_EXCEPTION.getErrorMessage());
        }
        if (this.sections.size() > 0 && isNoMatchStation(section)) {
            throw new IllegalArgumentException(ErrorCode.NO_MATCH_STATION_EXCEPTION.getErrorMessage());
        }
        if (this.sections.size() > 0 && !isSameUpStation(section) && !isSameDownStation(section) && hasEndPointIssue(section)) {
            throw new IllegalArgumentException(ErrorCode.ADD_END_POINT_ISSUE.getErrorMessage());
        }
    }

    private boolean isNoMatchStation(Section newSection) {
        return sections.stream()
                .map(Section::toStations)
                .flatMap(Collection::stream)
                .distinct()
                .noneMatch(station -> newSection.isSameDownStationId(station) ||
                        newSection.isSameUpStationId(station));
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
}
