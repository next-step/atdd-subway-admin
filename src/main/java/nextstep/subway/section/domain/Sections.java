package nextstep.subway.section.domain;

import nextstep.subway.exception.NoSuchDataException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station_section.StationSection;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Section> sections = new HashSet<>();

    protected Sections() {}

    public Sections(Set<Section> sections) {
        this.sections = sections;
    }

    public void add(Section section) {
        sections.add(section);
    }

    public int size() {
        return sections.size();
    }

    public List<Section> toList() {
        return sections.stream().collect(Collectors.toList());
    }

    public Station upEndStation() {
        return upEndStationSection().getStation();
    }

    public Station downEndStation() {
        return downEndStationSection().getStation();
    }

    public Section upEndSection() {
        return upEndStationSection().getSection();
    }

    public Section downEndSection() {
        return downEndStationSection().getSection();
    }

    public StationSection downEndStationSection() {
        Set<StationSection> endStations = findEndStations();

        return endStations.stream()
                .filter(StationSection::isDownStationType)
                .findFirst()
                .orElseThrow(() -> new NoSuchDataException("하행 종점역이 존재하지 않습니다.", null, null, null));
    }

    public StationSection upEndStationSection() {
        Set<StationSection> endStations = findEndStations();

        return endStations.stream()
                .filter(StationSection::isUpStationType)
                .findFirst()
                .orElseThrow(() -> new NoSuchDataException("상행 종점역이 존재하지 않습니다.", null, null, null));
    }

    private Set<StationSection> findEndStations() {
        Set<StationSection> endStations = new HashSet<>();
        sections.stream().forEach(section -> section.findEndStations(endStations));
        return endStations;
    }

    public List<Station> stationsFromUpToDown() {
        List<Station> stations = new ArrayList<>();
        Station upEndStation = upEndStation();

        stations.add(upEndStation);
        addDownStation(stations, upEndStation);

        return stations;
    }

    private void addDownStation(List<Station> stations, Station upEndStation) {
        Station temp = upEndStation;
        while (temp.isExitsDownSection()) {
            Station downStation = temp.downSection().downStation();
            stations.add(downStation);
            temp = downStation;
        }
    }
}
