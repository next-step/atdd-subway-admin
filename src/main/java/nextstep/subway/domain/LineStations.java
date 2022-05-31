package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.List;

@Embeddable
public class LineStations {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<LineStation> list;

    public LineStations() {
    }

    public LineStations(List<LineStation> list) {
        this.list = list;
    }

    public List<LineStation> getList() {
        return list;
    }
}
