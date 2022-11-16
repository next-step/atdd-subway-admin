package nextstep.subway.domain;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class LineStations {

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private List<LineStation> lineStations = new ArrayList<>();

    public void add(LineStation lineStation) {
        this.lineStations.add(lineStation);
    }

    public List<LineStation> getList() {
        return lineStations;
    }

}
