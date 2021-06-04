package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line")
    private List<Section> sections = new ArrayList<>();

    public void remove(Section section) {
        this.sections.remove(section);
    }

    public void add(Section section) {
        this.sections.add(section);
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }
}
