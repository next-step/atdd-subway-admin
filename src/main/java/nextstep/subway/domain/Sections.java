package nextstep.subway.domain;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Embeddable
public class Sections implements Iterable<Section> {
    @OneToMany(mappedBy = "line")
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        sections.add(section);
    }

    @Override
    public Iterator<Section> iterator() {
        return sections.iterator();
    }
}
