package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST)
    private List<Section> sections = new ArrayList<>();


    public List<Station> getStationsInOrder() {
        List<Station> stations = new ArrayList<>();
        Optional<Station> station = findDownStationByUpStation(null);

        while (station.isPresent()) {
            Station nowStation = station.get();
            stations.add(nowStation);
            station = findDownStationByUpStation(nowStation);
        }
        return stations;
    }

    private Optional<Station> findDownStationByUpStation(Station station) {
        return sections.stream()
                .filter(it -> it.getUpStation() == station)
                .findFirst()
                .map(Section::getDownStation);
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Section section, Line line) {
        if (!this.sections.contains(section)) {
            this.sections.add(section);
        }

        if (section.getLine() != line) {
            section.setLine(line);
        }
    }

    public void addSection(Station upStation, Station downStation, Integer distance, Line line) {
        if (sections.isEmpty()) {
            addInitialSections(upStation, downStation, distance, line);
            return;
        }

        Optional<Section> first = sections.stream()
                .filter(it -> it.getUpStation() == upStation)
                .findFirst();

        if (first.isPresent()) {
            Section section = first.get();
            Section newSection = section.changeDownStation(downStation, distance);
            addSection(newSection, line);
        }

    }

    private void addInitialSections(Station upStation, Station downStation, Integer distance, Line line) {
        List<Section> initialSections = Section.makeInitialSections(upStation, downStation, distance);
        initialSections.forEach(section -> addSection(section, line));
    }

}
