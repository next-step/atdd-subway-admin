package nextstep.subway.section.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.station.domain.Station;

@Embeddable
public class LineSections implements Serializable {

    private static final long serialVersionUID = -4483053178441994936L;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private final List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        sections.add(section);
    }

    public List<Station> toStations() {

        Map<Station, Station> map = new HashMap<>();
        for (Section section : sections) {
            map.put(section.getDownStation(), section.getUpStation());
        }

        Station start = null;
        for (Entry<Station, Station> entry : map.entrySet()) {
            if (!map.containsKey(entry.getValue())) {
                start = entry.getValue();
            }
        }

        map.clear();
        for (Section section : sections) {
            map.put(section.getUpStation(), section.getDownStation());
        }

        List<Station> stations = new ArrayList<>();
        stations.add(start);

        Station next = map.get(start);
        while (next != null) {
            stations.add(next);
            next = map.get(next);
        }

        return stations;
    }
}
