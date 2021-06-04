package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections;

    public Sections() {
        sections = new LinkedList<>();
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public List<Station> getStations() {
        List<Station> stations = new LinkedList<>();
        for (Section section : sections) {
            stations.addAll(section.getStations());
        }
        return stations;
    }

}
