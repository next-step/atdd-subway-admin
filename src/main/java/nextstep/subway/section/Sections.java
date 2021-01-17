package nextstep.subway.section;

import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", orphanRemoval = true)
    private final List<Section> element;

    public Sections() {
        this.element = new ArrayList<>();
    }

    public Sections(List<Section> element) {
        this.element = element;
    }

    public void addSection(Section section) {
        element.add(section);
    }

    public void removeSection(Section section) {
        element.remove(section);
    }

    public List<Station> toStations() {
        List<Station> stations = new ArrayList<>();
        Station upStation = findUpStation();
        stations.add(upStation);
        addSortByUpStation(upStation, stations);
        return stations;
    }

    private Station findUpStation() {
        Station downStation = this.element.get(0).getUpStation();
        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> findStation = element.stream()
                    .filter(section -> section.getDownStation() == finalDownStation)
                    .findFirst();
            if (!findStation.isPresent()) {
                break;
            }
            downStation = findStation.get().getUpStation();
        }
        return downStation;
    }

    private void addSortByUpStation(Station downStation, List<Station> stations) {
        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> findStation = this.element.stream()
                    .filter(section -> section.getUpStation() == finalDownStation)
                    .findFirst();
            if (!findStation.isPresent()) {
                break;
            }
            downStation = findStation.get().getDownStation();
            stations.add(downStation);
        }
    }

}
