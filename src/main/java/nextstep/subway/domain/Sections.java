package nextstep.subway.domain;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<Section> sections;

    protected Sections() { }

    public Sections(List<Section> sections) {
        this.sections = new LinkedList<>(sections);
    }

    public List<Station> assignedStations() {
        return this.sections.stream()
                .map(Section::stations)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
