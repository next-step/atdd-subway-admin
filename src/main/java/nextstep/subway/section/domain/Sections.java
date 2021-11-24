package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections of() {
        return new Sections();
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public void add(Section section) {
        this.sections.add(section);
    }

    public List<Station> orderedStations() {
        List<Station> stations = new ArrayList<>();
        for (Section section : this.sections) {
            stations.add(section.getUpStation());
        }
        stations.add(this.sections.get(this.sections.size() - 1).getDownStation());
        return stations;
    }
}
