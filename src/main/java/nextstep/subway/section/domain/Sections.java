package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections;

    protected Sections() {
        this.sections = new ArrayList<>();
    }

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections empty() {
        return new Sections();
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public void add(Section section) {
        if (!this.sections.isEmpty() && !isEdgeSection(section)) {
            relocateConnectSection(section);
        }
        this.sections.add(section);
    }

    public int size() {
        return this.sections.size();
    }

    public List<Section> get() {
        return this.sections;
    }

    public boolean containsStation(Station station) {
        return sortedLineStations().contains(station);
    }

    public int indexOfStation(Station station) {
        return sortedLineStations().indexOf(station);
    }

    public List<StationResponse> allStationResponses() {
        return sortedLineStations().toStationResponses();
    }

    private LineStations sortedLineStations() {
        List<Station> sortedAllStations = new ArrayList<>();
        sortedAllStations.add(findEdgeUpSection().getUpStation());
        for (int i = 0; i < this.sections.size(); i++) {
            Station nextStation = findSectionByUpStation(sortedAllStations.get(i))
                    .map(Section::getDownStation)
                    .orElseThrow(IllegalArgumentException::new);
            sortedAllStations.add(nextStation);
        }
        return LineStations.from(sortedAllStations);
    }

    private boolean isEdgeSection(Section addedSection) {
        return isEdgeUpSection(addedSection) || isEdgeDownSection(addedSection);
    }

    private boolean isEdgeUpSection(Section addedSection) {
        return findEdgeUpSection().getUpStation().equals(addedSection.getDownStation());
    }

    private boolean isEdgeDownSection(Section addedSection) {
        return findEdgeDownSection().getDownStation().equals(addedSection.getUpStation());
    }

    private Section findEdgeUpSection() {
        List<Station> downStations = this.sections.stream()
                .map(Section::getDownStation).collect(Collectors.toList());
        return this.sections.stream()
                .filter(section -> !downStations.contains(section.getUpStation()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private Section findEdgeDownSection() {
        List<Station> upStations = this.sections.stream()
                .map(Section::getUpStation).collect(Collectors.toList());
        return this.sections.stream()
                .filter(section -> !upStations.contains(section.getDownStation()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private void relocateConnectSection(Section addedSection) {
        Section connectSection = findConnectSection(addedSection);
        relocateUpSection(connectSection, addedSection);
        relocateDownSection(connectSection, addedSection);
    }

    private void relocateUpSection(Section connectSection, Section addedSection) {
        if (connectSection.isEqualsDownStation(addedSection.getDownStation())) {
            connectSection.updateDownStation(addedSection.getUpStation());
            connectSection.decreaseDistance(addedSection.getDistance());
        }
    }

    private void relocateDownSection(Section connectSection, Section addedSection) {
        if (connectSection.isEqualsUpStation(addedSection.getUpStation())) {
            connectSection.updateUpStation(addedSection.getDownStation());
            connectSection.decreaseDistance(addedSection.getDistance());
        }
    }

    private Section findConnectSection(Section section) {
        return findSectionByUpStation(section.getUpStation())
                .orElseGet(() -> findSectionByDownStation(section.getDownStation())
                        .orElseThrow(IllegalArgumentException::new)
                );
    }

    private Optional<Section> findSectionByUpStation(Station upStation) {
        return this.sections.stream()
                .filter(section -> section.isEqualsUpStation(upStation))
                .findFirst();
    }

    private Optional<Section> findSectionByDownStation(Station downStation) {
        return this.sections.stream()
                .filter(section -> section.isEqualsDownStation(downStation))
                .findFirst();
    }
}
