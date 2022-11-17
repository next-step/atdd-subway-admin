package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private final List<Section> sections = new ArrayList<>();

    protected Sections() { }

    public void add(Section section) {
        sections.add(section);
    }

    public List<Map<String, Object>> getStations() {
        List<Map<String, Object>> stations = sections.stream()
                .map(section -> section.getUpStation().toMapForOpen())
                .collect(Collectors.toList());
        Section lastSection = sections.get(sections.size() - 1);
        stations.add(lastSection.getDownStation().toMapForOpen());
        return stations;
    }
}
