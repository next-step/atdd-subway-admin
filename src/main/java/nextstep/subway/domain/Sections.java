package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> elements;

    protected Sections() {
        this.elements = new ArrayList<>();
    }

    private Sections(List<Section> sections) {
        this.elements = sections;
    }

    public static Sections empty() {
        return new Sections();
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public void add(Section section) {
        elements.add(section);
    }

    public List<Section> get() {
        return elements;
    }
}
