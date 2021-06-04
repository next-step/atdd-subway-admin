package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> sections() {
        return sections;
    }

    public void add(Section section) {
        sections.add(section);
    }

    public List<Station> stations() {
        List<Station> stations = new ArrayList<>();

        List<Station> upStations = sections.stream().map(Section::getUpStation).collect(Collectors.toList());
        List<Station> downStations = sections.stream().map(Section::getDownStation).collect(Collectors.toList());

        Station firstStation = findNotContainStation(upStations, downStations);
        Station lastStation = findNotContainStation(downStations, upStations);

        for(Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }
        return stations;
    }

    private Station findNotContainStation(List<Station> toFindStations, List<Station> criteriaStations) {
        return toFindStations.stream()
                .filter(toFindStation -> !criteriaStations.contains(toFindStation))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }
}
