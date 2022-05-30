package nextstep.subway.domain;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class LineStations {

    @OneToMany
    @JoinColumn(name = "line_id")
    private List<LineStation> lineStations = new ArrayList<>();

    protected LineStations() {
    }

    public void add(LineStation lineStation) {
        if (lineStations.isEmpty()) {
            lineStations.add(lineStation);
            return;
        }
    }

    public List<LineStation> getLineStations() {
        return lineStations;
    }

    public int size() {
        return lineStations.size();
    }

    public LineStation getFirst() {
        return lineStations.get(0);
    }
}
