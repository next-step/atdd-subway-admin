package nextstep.subway.domain.line;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line")
    private List<Section> sections = new ArrayList<>();

    protected Sections() {

    }

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections addFirstSection(Section section) {
        return new Sections(Collections.singletonList(section));
    }

    public List<Section> getSections() {
        return sections;
    }

    public int size() {
        return sections.size();
    }
}
