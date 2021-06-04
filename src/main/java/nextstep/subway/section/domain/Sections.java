package nextstep.subway.section.domain;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line")
    private List<Section> sections = new LinkedList<>();

    public void remove(Section section) {
        this.sections.remove(section);
    }

    public void add(Section section) {
        this.sections.add(section);
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public List<Station> toStations() {
        List<Station> stations = new LinkedList<>();
        for (Section section : sections) {
            stations.addAll(section.toStations());
        }
        return stations;
    }
}
