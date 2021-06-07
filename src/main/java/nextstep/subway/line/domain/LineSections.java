package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

@Embeddable
public class LineSections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> lineSections = new ArrayList<>();

    public LineSections(List<Section> lineSections) {
        this.lineSections = lineSections;
    }

    public LineSections() {
    }

    public void add(Section section) {
        this.lineSections.add(section);
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(lineSections);
    }

    public List<Station> toStations() {
        List<Station> stations = new ArrayList<>();
        for (Section section : lineSections) {
            stations.addAll(section.toStations());
        }
        return stations;
    }

    public List<Section> getOrderLineSections() {
        List<Section> orderedSections = new ArrayList<>();
        return orderedSections;
    }
}
