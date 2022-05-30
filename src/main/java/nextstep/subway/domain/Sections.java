package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> elements = new ArrayList<>();

    public void add(Section section) {
        repairSections(section);
        elements.add(section);
    }

    private void repairSections(Section section) {
        for (Section element : elements) {
            element.repair(section);
        }
    }

    public List<Section> getElements() {
        return Collections.unmodifiableList(elements);
    }
}
