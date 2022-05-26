package nextstep.subway.section.domain;

import static nextstep.subway.section.domain.exception.SectionExceptionMessage.*;

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
import nextstep.subway.section.domain.exception.SectionExceptionMessage;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
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

    private boolean isEndOfStation(Section section) {
        Section firstSection = this.findFirstSection();
        Section lastSection = this.findLastSection();

        return firstSection.isEqualsUpStation(section.getDownStation())
            || lastSection.isEqualsDownStation(section.getUpStation());
    }

    private Section findFirstSection() {
        List<Station> downStations = this.findDownStations();
        return this.sections.stream()
            .filter(section -> !downStations.contains(section.getUpStation()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("첫번째 Section을 못찾음"));
    }

    private Section findLastSection() {
        List<Station> upStations = this.findUpStations();
        return this.sections.stream()
            .filter(section -> !upStations.contains(section.getDownStation()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("마지막 Section을 못찾음"));
    }

    public long allocatedStationCount() {
        return this.getAllStations().size();
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
        return this.sections.stream()
            .filter(section -> section.isEqualsDownStation(station))
            .findFirst();
    }

    private Optional<Section> findSectionByUpStation(Station station) {
        return this.sections.stream()
            .filter(section -> section.isEqualsUpStation(station))
            .findFirst();
    }

    public Optional<Section> findSectionBySectionId(Long sectionId) {
        return this.sections.stream()
            .filter(section -> section.getId().equals(sectionId))
            .findFirst();
    }

    public List<Station> findSortedStations() {
        Station firstStation = this.findFirstStation();

        List<Station> sortedStations = Lists.newArrayList();
        sortedStations.add(firstStation);

        for (int i = 0; i < this.sections.size(); i++) {
            Station station = this.findSectionByUpStation(sortedStations.get(i))
                .map(Section::getDownStation)
                .orElseThrow(() -> new IllegalStateException(NOT_FOUND_UP_STATION_BY_SECTION.getMessage()));

            sortedStations.add(station);
        }

        return Collections.unmodifiableList(sortedStations);
    }

    private Station findFirstStation() {
        List<Station> downStations = this.findDownStations();
        Optional<Section> firstStation = this.sections.stream()
            .filter(section -> !downStations.contains(section.getUpStation()))
            .findFirst();

        if (!firstStation.isPresent()) {
            throw new IllegalStateException(NOT_FOUND_FIRST_STATION.getMessage());
        }

        return firstStation.get().getUpStation();
    }

    private List<Station> findDownStations() {
        return this.sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());
    }

    private List<Station> findUpStations() {
        return this.sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());
    }
}
