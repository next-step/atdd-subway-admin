package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        this.sections.add(section);
    }

    public int size() {
        return this.sections.size();
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        Station firstUpStation = findFirstUpStation();
        stations.add(firstUpStation);
        Station firstDownStation = findDownStationByUpStation(firstUpStation);
        stations.add(firstDownStation);
        Station upStation = firstDownStation;
        while (!isLastDownStation(upStation)) {
            addStation(stations, upStation);
            upStation = findDownStationByUpStation(upStation);
        }
        return stations;
    }

    private Station findFirstUpStation() {
        Station firstUpStation = this.sections.get(0).getUpStation();
        for (Section section : this.sections) {
            firstUpStation = findUpStation(firstUpStation, section);
        }
        return firstUpStation;
    }

    private Station findDownStationByUpStation(Station upStation) {
        Station downStation = upStation;
        for (Section section : this.sections) {
            downStation = findDownStationByUpStation(upStation, downStation, section);
        }
        return downStation;
    }

    private boolean isLastDownStation(Station upStation) {
        return this.sections
                .stream()
                .noneMatch(section -> upStation.equals(section.getUpStation()));
    }

    private static Station findDownStationByUpStation(Station upStation, Station downStation, Section section) {
        if (upStation.equals(section.getUpStation())) {
            downStation = section.getDownStation();
        }
        return downStation;
    }

    private void addStation(List<Station> stations, Station upStation) {
        this.sections.stream()
                .filter(section -> upStation.equals(section.getUpStation()))
                .map(Section::getDownStation)
                .forEach(stations::add);
    }

    private static Station findUpStation(Station firstUpStation, Section section) {
        if (firstUpStation.equals(section.getDownStation())) {
            firstUpStation = section.getUpStation();
        }
        return firstUpStation;
    }
}
