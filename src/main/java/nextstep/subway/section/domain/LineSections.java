package nextstep.subway.section.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.station.domain.SortedStations;
import nextstep.subway.station.domain.Stations;

@Embeddable
public class LineSections implements Serializable {

    private static final long serialVersionUID = -4483053178441994936L;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private final List<Section> sections;

    public LineSections() {
        sections = new ArrayList<>();
    }

    public LineSections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section section) {
        sections.add(section);
    }

    public List<Section> getSections() {
        return sections;
    }

    public Stations toStations() {
        return new SortedStations(this);
    }
}
