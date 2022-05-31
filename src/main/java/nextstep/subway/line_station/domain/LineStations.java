package nextstep.subway.line_station.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Embeddable
public class LineStations {

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "line")
    private List<LineStation> lineStations = new ArrayList<>();

    public void add(LineStation lineStation, Line line) {
        initEndPoint(lineStation, line);
        addLineStations(lineStation, line);
    }

    private void initEndPoint(LineStation lineStation, Line line) {
        if (lineStations.size() == 0) {
            addLineStations(lineStation.upStationEndPoint(lineStation), line);
        }
    }

    private void addLineStations(LineStation lineStation, Line line) {
        lineStations.add(lineStation);
        lineStation.changeLine(line);
    }

    public List<Station> orderStationsOfLine() {
        Optional<LineStation> lineStation = lineStations.stream()
            .filter(LineStation::isFirstStation)
             .findFirst();

        List<Station> stations = new ArrayList<>();
        while (lineStation.isPresent()) {
            LineStation preLineStation = lineStation.get();
            stations.add(preLineStation.getDownStation());
            lineStation = this.downStationOfLine(preLineStation);
        }

        return stations;
    }

    private Optional<LineStation> downStationOfLine(LineStation preLineStation) {
        return lineStations.stream()
            .filter(lineStation -> lineStation.isDownStation(preLineStation))
            .findFirst();
    }

}
