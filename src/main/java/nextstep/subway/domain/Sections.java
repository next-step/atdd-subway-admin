package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Section> items = new ArrayList<>();


    public List<Section> getItems() {
        return items;
    }

    public void add(Section of) {

    }
}
