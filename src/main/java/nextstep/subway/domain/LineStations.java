package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class LineStations {

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<LineStation> lineStations = new ArrayList<>();

    protected LineStations() {
    }

    public LineStations(List<LineStation> lineStations) {
        this.lineStations = lineStations;
    }

    public void add(LineStation lineStation) {
        lineStations.add(lineStation);
    }
}
