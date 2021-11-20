package nextstep.subway.section.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void add(final Section section) {
        this.sections.add(section);
    }

    public void remove(final Section section) {
        this.sections.remove(section);
    }

    public List<Section> getSections() {
        return sections;
    }

}
