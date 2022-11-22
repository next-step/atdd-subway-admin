package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    //@JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public List<Section> getSections() {
        return sections;
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections from(Section section) {
        List<Section> sections = new ArrayList<>();
        sections.add(section);
        return new Sections(sections);
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public List<Station> getStations() {
        return sections.stream().map(section -> section.getStations()).distinct().flatMap(Collection::stream).collect(Collectors.toList());
    }
}
