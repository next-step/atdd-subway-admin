package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class LineStations {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<LineStation> list;

    public LineStations() {
        list = new ArrayList<>();
    }

    public LineStations(List<LineStation> list) {
        this.list = list;
    }

    public void add(LineStation lineStation) {
        list.add(lineStation);
    }

    public List<LineStation> getList() {
        return list;
    }
}
