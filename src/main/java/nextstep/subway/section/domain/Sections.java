package nextstep.subway.section.domain;

import static nextstep.subway.section.domain.exception.SectionExceptionMessage.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import nextstep.subway.station.domain.Station;

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

    public List<Station> getStations() {
        List<Station> result = new ArrayList<>();
        for (Section section : this.sections) {
            result.add(section.getUpStation());
            result.add(section.getDownStation());
        }

        return result;
    }

    public void add(Section section) {
        if (!this.sections.isEmpty()) {
            Section middleSection = findMiddleSection(section);
            validateAddableSectionDistance(middleSection, section);
            adjustMiddleSection(middleSection, section);
        }

        this.sections.add(section);
    }

    public long allocatedStationCount() {
        return this.getStations().stream().distinct().count();
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
}
