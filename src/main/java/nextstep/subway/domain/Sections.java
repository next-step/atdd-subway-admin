package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Section> sectionList = new ArrayList<>();

    public List<Section> getSectionList() {
        return sectionList;
    }

    public void add(Section section) {
        sectionList.add(section);
    }
}
