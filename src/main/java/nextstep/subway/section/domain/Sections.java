package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line")
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section section) {
        this.sections.add(section);
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
