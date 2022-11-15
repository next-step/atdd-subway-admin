package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST)
    private List<Section> sections = new ArrayList<>();

    public List<Station> getStations() {
        Set<Station> stations = new HashSet<>();
        sections.forEach(section -> {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        });
        return stations.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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

    public void insertNewSection(Station upStation, Station downStation, Integer distance, Line line) {
        addSection(new Section(upStation, downStation, distance), line);
    }
}
