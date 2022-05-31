package nextstep.subway.domain;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id", nullable = false)
    private List<Section> sectionList = new LinkedList<>();

    protected Sections(){

    }

    public List<Section> getSectionList() {
        return Collections.unmodifiableList(sectionList);
    }

    public void add(Section section) {
        sectionList.add(section);
    }
}
