package nextstep.subway.line.domain;

import nextstep.subway.exception.BadRequestException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {

    }

    public void add(Section section) {
        sections.add(section);
    }

    public List<Station> getOrderedStations() {
        if (sections.isEmpty()) {
            return new ArrayList<>();
        }

        Station firstUpStation = findFirstUpStation();
        Station lastDownStation = findLastDownStation();

        return makeOrderedStations(firstUpStation, lastDownStation);
    }

    private List<Station> makeOrderedStations(Station firstStation, Station lastDownStation) {
        List<Station> orderedStations = new ArrayList<>();
        orderedStations.add(firstStation);

        Station nextStation = firstStation;
        while (!lastDownStation.equals(nextStation)) {
            nextStation = findNextStation(nextStation);
            orderedStations.add(nextStation);
        }

        return orderedStations;
    }

    private Station findNextStation(Station station) {
        return sections.stream()
                .filter(section -> section.isEqualsUpStation(station))
                .map(Section::getDownStation)
                .findFirst()
                .orElseThrow(BadRequestException::new);
    }

    private Station findFirstUpStation() {
        List<Station> allUpStations = getAllUpStations();
        List<Station> allDownStations = getAllDownStations();
        allUpStations.removeAll(allDownStations);
        return allUpStations.get(0);
    }

    private Station findLastDownStation() {
        List<Station> allUpStations = getAllUpStations();
        List<Station> allDownStations = getAllDownStations();
        allDownStations.removeAll(allUpStations);
        return allDownStations.get(0);
    }

    private List<Station> getAllUpStations() {
        return sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    private List<Station> getAllDownStations() {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }
}