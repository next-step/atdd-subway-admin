package nextstep.subway.line.domain;

import java.util.Collections;
import java.util.Set;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line")
    private Set<Section> sections;

    protected Sections() {
    }

    public Sections(final Set<Section> sections) {
        this.sections = sections;
    }

    public void add(final Section section) {
        this.sections.add(section);
    }

    public Set<Section> getSections() {
        return Collections.unmodifiableSet(sections);
    }
}
