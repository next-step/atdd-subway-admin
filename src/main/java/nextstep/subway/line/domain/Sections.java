package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line")
    private List<Section> sections = Collections.unmodifiableList(new ArrayList<>());

    protected Sections() {
    }

    private Sections(List<Section> sections) {
        this.sections = Collections.unmodifiableList(sections);
    }

    public Sections merge(Section addSection){
        List<Section> afterSections = new ArrayList<>(this.sections);
        afterSections.add(addSection);
        return new Sections(afterSections);
    }

    public List<Section> getSections() {
        return sections;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Sections sections1 = (Sections)o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}
