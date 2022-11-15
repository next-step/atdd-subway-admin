package nextstep.subway.domain;

import javax.persistence.*;
import java.util.*;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public Sections() {

    }

    public Sections(Section section) {
        sections = Arrays.asList(section);
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }
}
