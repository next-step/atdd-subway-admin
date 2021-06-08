package nextstep.subway.line.domain.wrappers;

import nextstep.subway.section.domain.Section;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(Section otherSection) {
        if (!contains(otherSection)) {
            sections.add(otherSection);
        }
    }

    public boolean contains(Section section) {
        return sections.contains(section);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Sections sections1 = (Sections) object;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}
