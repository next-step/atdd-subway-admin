package nextstep.subway.line.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    void addSection(Line line, Section section) {
        if(!hasSection(section)) {
            this.sections.add(section);
            section.updateLine(line);
        }
    }

    boolean hasSection(Section section) {
        return this.sections.contains(section);
    }

    List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }
}
