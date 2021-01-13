package nextstep.subway.section;

import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    public void sortBy() {
        element.sort(Section::compareTo);
    }

    public List<Station> toStations() {
        List<Station> stations = new ArrayList<>();
        for (Section section : this.element) {
            addStation(stations, section.getUpStation());
            addStation(stations, section.getDownStation());
        }
        return stations;
    }

    private void addStation(List<Station> stations, Station station) {
        if (!stations.contains(station)) {
            stations.add(station);
        }
    }

}
