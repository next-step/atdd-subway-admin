package nextstep.subway.section.domain;

import nextstep.subway.line.dto.Stations;
import nextstep.subway.station.domain.Station;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line")
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void add(Section section) {
        sections.add(section);
    }

    public boolean contains(Section section) {
        return sections.contains(section);
    }


    //해당단계에서는 하나의 섹션에서 상행 종점, 하행 종점만 있으므로 유일한 섹션의 상행과 하행 순으로 출력한다.
    public Stations getStations() {
        Stations stations = new Stations();
        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }
        return stations;
    }
}
