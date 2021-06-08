package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
public class Sections {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "line")
    private final List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        this.sections.add(section);
    }

    public List<Station> stations() {
        List<Station> stations = new ArrayList<>();

        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }

        return Collections.unmodifiableList(stations);
    }
}
