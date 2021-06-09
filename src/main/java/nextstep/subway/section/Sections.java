package nextstep.subway.section;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void add(Section section) {
        sections.add(section);
    }

    public boolean contains(Section section) {
        return sections.contains(section);
    }

    public List<Section> getAllSection() {
        return sections;
    }
}
