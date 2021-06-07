package nextstep.subway.section.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<Section> sections = new ArrayList<>();

    public Sections() { }

    public Stream<Section> stream() {
        return sections.stream();
    }

    public void add(Section section) {
        if (section == null) {
            throw new IllegalArgumentException("Section is null.");
        }
        if (!this.contains(section)) {
            sections.add(section);
        }
    }

    public boolean contains(Section section) {
        return sections.contains(section);
    }
}
