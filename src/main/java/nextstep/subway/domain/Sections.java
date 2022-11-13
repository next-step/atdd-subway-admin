package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    protected Sections() {}

    public List<Station> getStations() {
        return sections.stream()
                .map(Section::findStations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public void add(Section newSection) {
        sections.forEach(section -> section.update(newSection));
        sections.add(newSection);
    }
}
