package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<Section> sections;

    protected Sections() {}

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public static Sections empty() {
        return new Sections(new ArrayList<>());
    }

    public void add(Section section) {
        this.sections.add(section);
    }

    public boolean contains(Section section) {
        return this.sections.contains(section);
    }

    public List<Station> getStations() {
        List<Station> result = new ArrayList<>();
        for (Section section : this.sections) {
            result.add(section.getUpStation());
            result.add(section.getDownStation());
        }
        return result;
    }
}
