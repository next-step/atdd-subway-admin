package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Section> sectionList = new ArrayList<>();

    protected Sections() {
    }

    public Sections(final List<Section> sectionList) {
        this.sectionList = Collections.unmodifiableList(sectionList);
    }

    public void addSection(Section section) {
        this.sectionList.add(section);
    }

}
