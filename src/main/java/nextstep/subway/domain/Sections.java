package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

public class Sections {

    private List<Section> list;

    public Sections() {
        list = new ArrayList<>();
    }

    public Sections(List<Section> list) {
        this.list = list;
    }

    public List<Section> getList() {
        return list;
    }
}
