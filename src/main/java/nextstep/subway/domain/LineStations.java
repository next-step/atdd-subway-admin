package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

@Embeddable
public class LineStations {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<LineStation> lineStationList = new LinkedList<>();

    protected LineStations() {
    }

    public void addStation(Line line, Station upStation, Station downStation, Integer distance) {
        LineStation lineStation = new LineStation(line, upStation, downStation, distance);

        lineStationList.add(lineStation);
    }

    public Stream<LineStation> stream() {
        return lineStationList.stream();
    }
}
