package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(Station upStation, Station downStation) {
        sections.add(new Section(null, upStation, 0));
        sections.add(new Section(upStation, downStation, 7));
    }
}
