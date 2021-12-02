package nextstep.subway.section.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        //TODO
        sections.add(section);
    }

    public List<Long> getStations() {
        List<Long> stations = new ArrayList<>();
        if (Objects.isNull(sections) || sections.size() == 0) {
            return stations;
        }

        stations.add(sections.get(0).getUpStationId());
        stations.addAll(sections.stream()
                .map(section -> section.getDownStationId())
                .collect(Collectors.toList()));

        return stations;
    }
}
