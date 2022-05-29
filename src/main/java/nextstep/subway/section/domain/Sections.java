package nextstep.subway.section.domain;

import static nextstep.subway.section.domain.exception.SectionExceptionMessage.NEW_SECTION_DISTANCE_IS_GREATER_OR_EQUALS;
import static nextstep.subway.section.domain.exception.SectionExceptionMessage.NOT_FOUND_SECTION;
import static nextstep.subway.section.domain.exception.SectionExceptionMessage.NOT_FOUND_SECTION_BY_STATION;
import static nextstep.subway.section.domain.exception.SectionExceptionMessage.NOT_FOUND_UP_STATION_BY_SECTION;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import nextstep.subway.line.domain.LineStations;
import nextstep.subway.station.domain.Station;
import nextstep.subway.utils.StreamUtils;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections;

    protected Sections() {}

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public static Sections empty() {
        return new Sections(new ArrayList<>());
    }

    public boolean contains(Section section) {
        return this.sections.contains(section);
    }

    public void add(Section section) {
        if (!this.sections.isEmpty() && !this.isEndOfStation(section)) {
            Section middleSection = findMiddleSection(section);
            validateAddableSectionDistance(middleSection, section);
            adjustMiddleSection(middleSection, section);
        }

        this.sections.add(section);
    }

    public Optional<Section> findSectionBySectionId(Long sectionId) {
        return StreamUtils.filterAndFindFirst(this.sections,
            section -> section.getId().equals(sectionId));
    }

    public LineStations findSortedStations() {
        List<Station> sortedStations = Lists.newArrayList();
        sortedStations.add(findFirstSection().getUpStation());

        for (int i = 0; i < this.sections.size(); i++) {
            Station station = this.findSectionByUpStation(sortedStations.get(i))
                .map(Section::getDownStation)
                .orElseThrow(() -> new IllegalStateException(NOT_FOUND_UP_STATION_BY_SECTION.getMessage()));

            sortedStations.add(station);
        }

        return LineStations.from(sortedStations);
    }

    public boolean containUpDownStation(Section section) {
        return this.isExistStation(section.getUpStation()) && this.isExistStation(section.getDownStation());
    }

    public boolean isEmpty() {
        return this.sections.isEmpty();
    }

    public boolean isEndStation(Station station) {
        Section firstSection = this.findFirstSection();
        Section lastSection = this.findLastSection();

        return firstSection.isEqualsUpStation(station)
            || lastSection.isEqualsDownStation(station);
    }

    public void removeEndStation(Station station) {
        if (isFirstEndStation(station)) {
            removeStation(this.findFirstSection());
            return;
        }

        removeStation(this.findLastSection());
    }

    public void removeMiddleStation(Station station) {
        Section prevSection = findByDownStation(station);
        Section postSection = findByUpStation(station);

        rearrangeSection(prevSection, postSection);
        removeSection(postSection);
    }

    public boolean hasStation(Station station) {
        return this.findSortedStations().contains(station);
    }

    public boolean isOnlyOneSection() {
        return this.sections.size() == 1;
    }

    private void removeSection(Section section) {
        this.sections.remove(section);
    }

    private void rearrangeSection(Section prevSection, Section postSection) {
        prevSection.changeDownStation(postSection.getDownStation());
        prevSection.plusDistanceByDistance(postSection.getDistance());
    }

    private void removeStation(Section section) {
        this.sections.remove(section);
    }

    private boolean isFirstEndStation(Station station) {
        return this.findFirstSection().isEqualsUpStation(station);
    }

    private boolean isEndOfStation(Section section) {
        Section firstSection = this.findFirstSection();
        Section lastSection = this.findLastSection();

        return firstSection.isEqualsUpStation(section.getDownStation())
            || lastSection.isEqualsDownStation(section.getUpStation());
    }

    private Section findFirstSection() {
        List<Station> downStations = this.findDownStations();
        return StreamUtils.filterAndFindFirst(this.sections, section -> !downStations.contains(section.getUpStation()))
            .orElseThrow(() -> new IllegalStateException(NOT_FOUND_SECTION.getMessage()));
    }

    private Section findLastSection() {
        List<Station> upStations = this.findUpStations();
        return StreamUtils.filterAndFindFirst(this.sections, section -> !upStations.contains(section.getDownStation()))
            .orElseThrow(() -> new IllegalStateException(NOT_FOUND_SECTION.getMessage()));
    }

    private void validateAddableSectionDistance(Section middleSection, Section section) {
        if (section.isGreaterThanOrEqualsDistance(middleSection)) {
            throw new IllegalArgumentException(NEW_SECTION_DISTANCE_IS_GREATER_OR_EQUALS.getMessage());
        }
    }

    private void adjustMiddleSection(Section middleSection, Section section) {
        adjustMiddleSectionUpStation(middleSection, section);
        adjustMiddleSectionDownStation(middleSection, section);
    }

    private void adjustMiddleSectionDownStation(Section middleSection, Section section) {
        if (middleSection.isEqualsDownStation(section.getDownStation())) {
            middleSection.changeDownStation(section.getUpStation());
            middleSection.reduceDistanceByDistance(section.getDistance());
        }
    }

    private void adjustMiddleSectionUpStation(Section middleSection, Section section) {
        if (middleSection.isEqualsUpStation(section.getUpStation())) {
            middleSection.changeUpStation(section.getDownStation());
            middleSection.reduceDistanceByDistance(section.getDistance());
        }
    }

    private Section findMiddleSection(Section section) {
        return findSectionByUpStation(section.getUpStation())
            .orElseGet(() -> findSectionByDownStation(section.getDownStation())
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_SECTION_BY_STATION.getMessage())));
    }

    private Optional<Section> findSectionByDownStation(Station station) {
        return StreamUtils.filterAndFindFirst(this.sections,
            section -> section.isEqualsDownStation(station));
    }

    private Optional<Section> findSectionByUpStation(Station station) {
        return StreamUtils.filterAndFindFirst(this.sections,
            section -> section.isEqualsUpStation(station));
    }

    private List<Station> findDownStations() {
        return StreamUtils.mapToList(this.sections, Section::getDownStation);
    }

    private List<Station> findUpStations() {
        return StreamUtils.mapToList(this.sections, Section::getUpStation);
    }

    private boolean isExistStation(Station station) {
        return this.findSortedStations().contains(station);
    }

    private Section findByUpStation(Station station) {
        return this.findSectionByUpStation(station)
            .orElseThrow(() -> new IllegalStateException(NOT_FOUND_UP_STATION_BY_SECTION.getMessage()));
    }

    private Section findByDownStation(Station station) {
        return this.findSectionByDownStation(station)
            .orElseThrow(() -> new IllegalStateException(NOT_FOUND_UP_STATION_BY_SECTION.getMessage()));
    }

}
