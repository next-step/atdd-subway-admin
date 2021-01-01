package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;

public class LineStations {
    private final Map<Station, LineStation> lineStations;
    private final LineStation firstLineStation;

    public LineStations(List<LineStation> lineStations) {
        this.lineStations = unmodifiableMap(lineStations.stream()
                .collect(toMap(LineStation::getStation, Function.identity())));

        this.firstLineStation = lineStations.stream()
                .filter(ls -> ls.getPreviousStation() == null)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("기점 지하철역이 존재하지 않습니다"));
    }

    public List<Station> getSortedStations() {
        List<LineStation> lineStations = new ArrayList<>();
        addLineStationToLineStations(lineStations);
        return lineStations.stream()
                .map(LineStation::getStation)
                .collect(toList());
    }

    private LineStation getLineStationByStation(Station station) {
        return lineStations.get(station);
    }

    private void addLineStationToLineStations(List<LineStation> lineStations) {
        lineStations.add(firstLineStation);
        LineStation nextLineStation = getLineStationByStation(firstLineStation.getNextStation());

        while (nextLineStation != null) {
            lineStations.add(nextLineStation);
            nextLineStation = getLineStationByStation(nextLineStation.getNextStation());
        }
    }
}
