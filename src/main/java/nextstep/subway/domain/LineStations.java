package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;

public class LineStations {

    private List<LineStation> lineStations = new ArrayList<>();

    public LineStations() {
    }

    public List<LineStation> getLineStations() {
        return lineStations;
    }

    public void setLineStations(List<LineStation> lineStations) {
        this.lineStations = lineStations;
    }

    public boolean contains(LineStation lineStation) {
        return this.lineStations.contains(lineStation);
    }
}
