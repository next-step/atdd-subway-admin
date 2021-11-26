package nextstep.subway.section.domain;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line")
    private List<Section> sections = new ArrayList<>();

    public void add(final Section section) {
        sections.add(section);
        }

    public List<Section> getSections() {
        return Collections.unmodifiableList(this.sections);
    }

    public void remove(Section section) {
        sections.add(section);
    }
}
