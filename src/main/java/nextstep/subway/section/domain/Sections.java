package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "line")
    private List<Section> sections = new ArrayList<>();

    public void add(Section section, Line line) {
        initEndPoint(section, line);
        addSection(section, line);
    }

    private void initEndPoint(Section section, Line line) {
        if (sections.size() == 0) {
            addSection(section.upStationEndPoint(section), line);
        }
    }

    private void addSection(Section section, Line line) {
        sections.add(section);
        section.changeLine(line);
    }

    public List<Station> orderStationsOfLine() {
        Optional<Section> section = sections.stream()
            .filter(Section::isFirstStation)
             .findFirst();

        List<Station> stations = new ArrayList<>();
        while (section.isPresent()) {
            Section preSection = section.get();
            stations.add(preSection.getDownStation());
            section = this.downStationOfLine(preSection);
        }

        return stations;
    }

    private Optional<Section> downStationOfLine(Section preSection) {
        return sections.stream()
            .filter(section -> section.isDownStation(preSection))
            .findFirst();
    }

}
