package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void add(Section section) {
        sections.add(section);
    }

    public List<Station> stations() {
        List<Station> stations = new ArrayList<>();

        List<Station> upStations = gatherStations(Section::getUpStation);
        List<Station> downStations = gatherStations(Section::getDownStation);

        Station firstStation = findNotContainStation(upStations, downStations);
        Station lastStation = findNotContainStation(downStations, upStations);

        Station nextStation = firstStation;

        stations.add(firstStation);

        while (!nextStation.equals(lastStation)) {
            nextStation = nextStation(nextStation);
            stations.add(nextStation);
        }

        return stations;
    }

    private Station findNotContainStation(List<Station> toFindStations, List<Station> criteriaStations) {

        return toFindStations.stream()
                .filter(toFindStation -> !criteriaStations.contains(toFindStation))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    private List<Station> gatherStations(Function<Section, Station> getStation) {

        return sections.stream().map(getStation).collect(Collectors.toList());
    }

    private Station nextStation(Station nextStation) {

        return sections.stream()
                .filter(section -> section.isEqualToUpStation(nextStation))
                .findFirst().orElseThrow(NoSuchElementException::new)
                .getDownStation();
    }
}
