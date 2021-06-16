package nextstep.subway.line.domain.wrappers;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Embeddable
public class LineStations {

    public static final String NOT_FOUND_STATION_ERROR_MESSAGE = "%s을 종점으로 하는 구간은 존재하지 않습니다.";
    public static final int MIN_STATION_COUNT = 2;
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineStation> lineStations = new ArrayList<>();

    public LineStations() {
    }

    public LineStations(List<LineStation> lineStations) {
        this.lineStations = lineStations;
    }

    public void addLineStation(LineStation lineStation) {
        if (!contains(lineStation)) {
            lineStations.add(lineStation);
        }
    }

    public boolean contains(LineStation lineStation) {
        return lineStations.contains(lineStation);
    }

    public void addLine(Line line) {
        for (LineStation lineStation : lineStations) {
            lineStation.lineBy(line);
        }
    }

    public List<LineStation> getLineStationsOrderByAsc() {
        List<LineStation> lineStations = new LinkedList<>();
        Optional<LineStation> preLineStation = findFirstLineStation();
        while (preLineStation.isPresent()) {
            LineStation lineStation = preLineStation.get();
            lineStations.add(lineStation);
            preLineStation = findNextLineStation(lineStation);
        }
        return lineStations;
    }

    public LineStation findLineStationByPreStation(LineStation lineStation) {
        return lineStations
                .stream()
                .filter(ls -> ls.isSamePreStation(lineStation))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public void updateFirstLineStation(LineStation lineStation) {
        findFirstLineStation().ifPresent(
                ls -> ls.update(lineStation.getStation(), lineStation.getPreStation(), lineStation.getDistance()));
    }

    public boolean isNewUpLineStation(LineStation lineStation) {
        AtomicBoolean isNewUpLineStation = new AtomicBoolean(false);
        findFirstLineStation().ifPresent( ls -> isNewUpLineStation.set(ls.isSameStation(lineStation)));
        return isNewUpLineStation.get();
    }

    public boolean isNewDownLineStation(LineStation lineStation) {
        return getLineStationsOrderByAsc().get(lineStations.size() - 1).isLastLineStation(lineStation);
    }

    public boolean isNotContainStations(LineStation lineStation) {
        return lineStations.stream().anyMatch(ls -> ls.isNotContainStation(lineStation));
    }

    public List<Station> generateStations() {
        List<Station> stations = new LinkedList<>();
        Optional<LineStation> preLineStation = findFirstLineStation();
        while (preLineStation.isPresent()) {
            LineStation lineStation = preLineStation.get();
            stations.add(lineStation.getStation());
            preLineStation = findNextLineStation(lineStation);
        }
        return stations;
    }

    public Optional<LineStation> findNextLineStation(LineStation lineStation) {
        return lineStations
                .stream()
                .filter(ls -> ls.isNextLineStation(lineStation))
                .findFirst();
    }

    private Optional<LineStation> findFirstLineStation() {
        return lineStations.stream().filter(LineStation::isNullPreStation).findFirst();
    }

    public LineStation findLineStationByStation(Station station) {
        return lineStations.stream()
                .filter(lineStation -> lineStation.isSameStation(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format(NOT_FOUND_STATION_ERROR_MESSAGE, station.getName())
                ));
    }

    public boolean isSingleSection() {
        return getLineStationsOrderByAsc().size() == MIN_STATION_COUNT;
    }

    public void delete(LineStation lineStation) {
        lineStations.remove(lineStation);
    }

    public boolean isSameLineStation(LineStation other) {
        boolean isSame = false;
        for (LineStation lineStation : lineStations) {
            isSame = lineStation.isSame(other);
        }
        return isSame;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        LineStations that = (LineStations) object;
        return Objects.equals(lineStations, that.lineStations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineStations);
    }
}
