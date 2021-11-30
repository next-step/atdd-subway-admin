package nextstep.subway.station.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;

@Embeddable
public class Stations {
    @OneToMany(mappedBy = "line")
    private List<Station> stations;

    public Stations() {
    }

    public Stations(List<Station> stations) {
        this.stations = stations;
    }

    public static Stations generateStations(List<Section> sections, Line line) {
        List<Station> stations = new ArrayList<>();
        for (Section section : sections) {
            Station station = section.getUpStation();
            station.setLine(line);
            stations.add(station);
        }
        if (sections.size() > 0) {
            Station station = sections.get(sections.size() - 1).getDownStation();
            station.setLine(line);
            stations.add(station);
        }
        return new Stations(stations);
    }

    public List<Station> getStations() {
        return stations;
    }
}
