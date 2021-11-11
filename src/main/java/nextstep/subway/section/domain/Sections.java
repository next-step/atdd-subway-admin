package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embeddable;

@Embeddable
public class Sections {
    private List<Section> sections;

    protected Sections() {}

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections createEmpty() {
        return new Sections(new ArrayList<>());
    }
}
