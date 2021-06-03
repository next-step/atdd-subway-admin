package nextstep.subway.section.domain;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line")
    private List<Section> sections;

    protected Sections() {
        this.sections = new ArrayList<>();
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public int size() {
        return sections.size();
    }
}
