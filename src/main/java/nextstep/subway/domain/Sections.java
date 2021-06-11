package nextstep.subway.domain;

import nextstep.subway.exception.NoSuchDataException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {}

    public Sections(ArrayList<Section> sections) {
        this.sections = sections;
    }

    public void add(Section section) {
        sections.add(section);
    }

    public boolean contains(Section section) {
        return sections.contains(section);
    }
}
