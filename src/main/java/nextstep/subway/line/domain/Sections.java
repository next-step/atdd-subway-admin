package nextstep.subway.line.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections;

    protected Sections() {
        sections = new ArrayList<>();
    }

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public void add(Section section) {
        sections.add(section);
    }

    public void remove(Section section) {
        sections.remove(section);
    }

    public List<Section> sections() {
        return sections;
    }
}
