package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        this.sections.add(section);
    }

    public void remove(Section section) {
        this.sections.remove(section);
    }

    public List<Station> createStations() {
        return createStationsFromUpToDown(extractFirstSection());
    }

    private List<Station> createStationsFromUpToDown(Section section) {
        List<Station> stations = new ArrayList<>();
        stations.add(section.getUpStation());
        while (!isLastStation(section.getDownStation())) {
            Section extractSection = extractSectionByContainsUpStation(section.getDownStation());
            stations.add(extractSection.getUpStation());
            section = extractSection;
        }
        stations.add(section.getDownStation());
        return stations;
    }

    private Section extractFirstSection() {
        Set<Station> stations = createDownStations();
        return sections.stream()
            .filter(section -> !stations.contains(section.getUpStation()))
            .findFirst()
            .orElseThrow(NotFoundException::new);
    }

    private Section extractLastSection() {
        Set<Station> stations = createUpStations();
        return sections.stream()
            .filter(section -> !stations.contains(section.getDownStation()))
            .findFirst()
            .orElseThrow(NotFoundException::new);
    }

    private boolean isLastStation(Station downStation) {
        return createUpStations().stream()
            .noneMatch(station -> station.equals(downStation));
    }

    private Section extractSectionByContainsUpStation(Station station) {
        return sections.stream()
            .filter(section -> section.getUpStation().equals(station))
            .findFirst()
            .orElseThrow(NotFoundException::new);
    }

    private Set<Station> createAllStations() {
        Set<Station> upStations = createUpStations();
        upStations.addAll(createDownStations());
        return upStations;
    }

    private Set<Station> createUpStations() {
        return sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toSet());
    }

    private Set<Station> createDownStations() {
        return sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toSet());
    }

    public void addSection(Section toSection) {

    }
}
