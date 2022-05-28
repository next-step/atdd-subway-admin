package nextstep.subway.section.domain;

import static nextstep.subway.section.domain.exception.SectionExceptionMessage.NEW_SECTION_DISTANCE_IS_GREATER_OR_EQUALS;
import static nextstep.subway.section.domain.exception.SectionExceptionMessage.NOT_FOUND_SECTION;
import static nextstep.subway.section.domain.exception.SectionExceptionMessage.NOT_FOUND_SECTION_BY_STATION;
import static nextstep.subway.section.domain.exception.SectionExceptionMessage.NOT_FOUND_UP_STATION_BY_SECTION;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
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

    public List<Station> getAllStations() {
        List<Station> result = new ArrayList<>();
        for (Section section : this.sections) {
            result.add(section.getUpStation());
            result.add(section.getDownStation());
        }
        return result.stream().distinct().collect(Collectors.toList());
    }

    public void add(Section section) {
        if (!this.sections.isEmpty() && !this.isEndOfStation(section)) {
            Section middleSection = findMiddleSection(section);
            validateAddableSectionDistance(middleSection, section);
            adjustMiddleSection(middleSection, section);
        }

        this.sections.add(section);
    }

    public long allocatedStationCount() {
        return this.getAllStations().size();
    }

    public Optional<Section> findSectionBySectionId(Long sectionId) {
        return StreamUtils.filterAndFindFirst(this.sections,
            section -> section.getId().equals(sectionId));
    }

    public List<Station> findSortedStations() {
        List<Station> sortedStations = Lists.newArrayList();
        sortedStations.add(findFirstSection().getUpStation());

        for (int i = 0; i < this.sections.size(); i++) {
            Station station = this.findSectionByUpStation(sortedStations.get(i))
                .map(Section::getDownStation)
                .orElseThrow(() -> new IllegalStateException(NOT_FOUND_UP_STATION_BY_SECTION.getMessage()));

            sortedStations.add(station);
        }

        return Collections.unmodifiableList(sortedStations);
    }

    public boolean containUpDownStation(Section section) {
        return this.isExistStation(section.getUpStation()) && this.isExistStation(section.getDownStation());
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
        return this.getAllStations().contains(station);
    }
}
