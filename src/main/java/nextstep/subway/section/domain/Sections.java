package nextstep.subway.section.domain;

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
        if (sections.contains(section)) {
            return;
        }
        sections.add(section);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }
        return stations;
    }
}
