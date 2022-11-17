package nextstep.subway.line;

import nextstep.subway.station.domain.Station;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line")
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        this.sections.add(section);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        Station firstUpStation = findFirstUpStation();
        stations.add(firstUpStation);
        for (Section section : this.sections) {
            if (firstUpStation.equals(section.getUpStation())) {
                stations.add(section.getDownStation());
            }
        }
        while (isTrue(firstUpStation)) {
            if (isTrue(firstUpStation)) {
                for (Section section : this.sections) {
                    if (firstUpStation.equals(section.getUpStation())) {
                        stations.add(section.getDownStation());
                        firstUpStation = section.getUpStation();
                    }
                }
            }
        }
        for (int i = 0; i < this.sections.size(); i++) {
            stations.add(this.sections.get(0).getUpStation());
            if (i == this.sections.size()) {
                stations.add(this.sections.get(0).getDownStation());
            }
        }
        return stations;
    }

    private boolean isTrue(Station firstUpStation) {
        for (Section section : this.sections) {
            if (firstUpStation.equals(section.getUpStation())) {
                return true;
            }
        }
        return false;
    }

    private Station findFirstUpStation() {
        Station firstUpStation = this.sections.get(0).getUpStation();
        for (Section section : this.sections) {
            if (firstUpStation.equals(section.getDownStation())) {
                firstUpStation = section.getUpStation();
            }
        }
        return firstUpStation;
    }
}
