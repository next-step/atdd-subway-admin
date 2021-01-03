package nextstep.subway.section.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.REMOVE)
    @OrderBy("sectionNumber")
    private List<Section> items = new ArrayList<>();

    public void add(Section section) {
        items.add(section);
    }

    public boolean remove(Section section) {
        return items.remove(section);
    }

    public int size() {
        return items.size();
    }

    public Section get(int idx) {
        return items.get(idx);
    }

    public Stream<Section> stream() {
        return items.stream();
    }
}
