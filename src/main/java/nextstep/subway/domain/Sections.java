package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {

    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public void sectionsCheck(Section newSection) {
        for (Section section : sections) {
            section.validateCheck(newSection);
        }
    }

    public List<Section> getOrderSections() {
        List<Section> sections = new ArrayList<>();
        Section section = lineFirstSection();

        while (section != null) {
            sections.add(section);
            section = findNextSection(section);
        }
        return sections;
    }

    private Section lineFirstSection() {
        if (isEmpty()) {
            return null;
        }
        Section section = this.sections.get(size() - 1);
        while (findPreviousSection(section) != null) {
            section = findPreviousSection(section);
        }
        return section;
    }

    private Section findPreviousSection(Section section) {
        return this.sections.stream()
                .filter(value -> value.getDownStation().equals(section.getUpStation()))
                .findFirst()
                .orElse(null);
    }

    private Section findNextSection(Section section) {
        return this.sections.stream()
                .filter(value -> value.getUpStation().equals(section.getDownStation()))
                .findFirst()
                .orElse(null);
    }

    public int size() {
        return this.sections.size();
    }

    public boolean isEmpty() {
        return this.sections.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sections)) return false;
        Sections sections1 = (Sections) o;
        return Objects.equals(getSections(), sections1.getSections());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSections());
    }

    @Override
    public String toString() {
        return "Sections{" +
                '}';
    }
}
