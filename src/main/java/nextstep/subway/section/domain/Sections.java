package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        //TODO : 작업

        sections.add(section);
    }

    public Set<Station> lineUp() {
        LinkedHashSet<Station> stationLink = new LinkedHashSet();

        for (Section section : sections) {
            stationLink.add(section.getUpStation());
            stationLink.add(section.getDownStation());
        }

        return stationLink;
    }
}
