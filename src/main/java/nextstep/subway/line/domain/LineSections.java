package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        Optional<Section> preSection = findFirstSection();
        List<Section> orderedSections = new ArrayList<>();

        while (preSection.isPresent()) {
            Section curSection = preSection.get();
            orderedSections.add(curSection);
            preSection = findSameUpStation(curSection.getDownStation());
        }
        return orderedSections;
    }

    private Optional<Section> findFirstSection() {
        return lineSections.stream()
            .filter(section -> !isExistsDownStation(section.getUpStation()))
            .findFirst();
    }

    private boolean isExistsDownStation(Station station) {
        Optional<Section> findSection = lineSections.stream()
            .filter(section -> station.equals(section.getDownStation()))
            .findFirst();
        return findSection.isPresent();
    }

    private Optional<Section> findSameUpStation(Station station) {
        return lineSections.stream()
            .filter(section -> section.getUpStation().equals(station))
            .findFirst();
    }

}
