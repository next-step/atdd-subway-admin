package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

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

    public List<StationResponse> extractStations() {
        List<StationResponse> upStations = extractUpStations();
        List<StationResponse> downStations = extractDownStations();

        upStations.addAll(downStations);

        return checkDuplicateAndDistinctStations(upStations);
    }

    private List<StationResponse> checkDuplicateAndDistinctStations(List<StationResponse> upStations) {
        if (haveDuplicateStation(upStations)) {
            return removeDuplicateStations(upStations);
        }

        return upStations;
    }

    private List<StationResponse> removeDuplicateStations(List<StationResponse> stations) {
        return stations.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private boolean haveDuplicateStation(List<StationResponse> upStations) {
        return upStations.size() != new HashSet<>(upStations).size();
    }

    private List<StationResponse> extractUpStations() {
        return sections.stream()
                .map(section -> StationResponse.of(section.getUpStation()))
                .collect(Collectors.toList());
    }

    private List<StationResponse> extractDownStations() {
        return sections.stream()
                .map(section -> StationResponse.of(section.getDownStation()))
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
