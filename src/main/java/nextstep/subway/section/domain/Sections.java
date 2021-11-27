package nextstep.subway.section.domain;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line")
    private final List<Section> sectionGroup = new ArrayList<>();

    protected Sections() {
    }

    public static Sections empty() {
        return new Sections();
    }

    public void add(Section section) {
        sectionGroup.add(section);
    }

    public int size() {
        return sectionGroup.size();
    }
}
