package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Stations;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST)
    private final List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void add(Section section) {
        this.sections.add(section);
    }

    public List<Section> getSections() {
        return sections;
    }

    public Stations getAllStations() {
        return new Stations(this);
    }
}
