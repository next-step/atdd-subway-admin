package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line")
    private List<Section> sections;

    protected Sections() {}

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public static Sections empty() {
        return new Sections(new ArrayList<>());
    }

    public void add(Section section) {
        this.sections.add(section);
    }

    public boolean contains(Section section) {
        return this.sections.contains(section);
    }
}
