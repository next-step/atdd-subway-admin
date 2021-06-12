package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private final List<Section> sections = new ArrayList<>();

    public Sections() {

    }

    public void add(Section section) {
        sections.add(section);
    }

    public List<Station> stationsFromUpToDown() {
        List<Station> stations = new LinkedList<>();

        for (int i = 0; i < sections.size(); i++) {
            Section section = sections.get(i);
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }

        return stations;
    }
}
