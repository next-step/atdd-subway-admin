package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "lineId")
    private List<Section> sections = new ArrayList<>();

    public List<Station> getStations() {
        return sections.stream()
            .flatMap(section -> section.getStations().stream())
            .distinct()
            .collect(Collectors.toList());
    }

    public void add(Section newSection) {
        this.sections.forEach(section -> section.relocate(newSection));
        sections.add(newSection);
    }
}
