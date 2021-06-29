package nextstep.subway.section.domain;

import nextstep.subway.exception.CannotRemoveSingleSectionException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    public static final int MINIMUM_SECTION_SIZE_FOR_REMOVE = 1;

    public static final int START_INDEX = 0;
    public static final int LAST_INDEX = 1;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section section) {
        this.sections.add(section);
    }

    public void validateAndAddSections(long newDistance, Station newUpStation, Station newDownStation) {
        for (Section section : sections) {
            section.validateSectionAndAddSection(newDistance, newUpStation, newDownStation, new ArrayList<>(sections));
        }
    }

    public void validateAndRemoveSectionByStation(Station targetStation) {
        validateSingleSection();
        removeSectionByStation(targetStation);
    }

    private void validateSingleSection() {
        if (sections.size() <= MINIMUM_SECTION_SIZE_FOR_REMOVE) {
            throw new CannotRemoveSingleSectionException();
        }
    }

    public void removeSectionByStation(Station targetStation) {
        int sizeOfSections = sections.size();
        for (int index = START_INDEX; index < sizeOfSections; index++) {
            Section section = sections.get(index);

            section.removeSectionByStation(sections, targetStation, index);
        }
    }

    public List<Station> extractStations() {
        List<Station> upStations = extractUpStations();
        List<Station> downStations = extractDownStations();

        upStations.addAll(downStations);

        return checkDuplicateAndDistinctStations(upStations);
    }

    private List<Station> checkDuplicateAndDistinctStations(List<Station> upStations) {
        if (haveDuplicateStation(upStations)) {
            return removeDuplicateStations(upStations);
        }

        return upStations;
    }

    private List<Station> removeDuplicateStations(List<Station> stations) {
        return stations.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private boolean haveDuplicateStation(List<Station> upStations) {
        return upStations.size() != new HashSet<>(upStations).size();
    }

    private List<Station> extractUpStations() {
        return sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    private List<Station> extractDownStations() {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    public List<Section> getSections() {
        return sections;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sections sections1 = (Sections) o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}
