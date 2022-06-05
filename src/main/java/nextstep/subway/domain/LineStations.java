package nextstep.subway.domain;

import javax.persistence.EntityExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class LineStations {
    private final List<LineStation> lineStations = new ArrayList<>();

    protected LineStations() {

    }

    public LineStations(List<LineStation> lineStations) {
        validation(lineStations);
        this.lineStations.addAll(lineStations);
    }


    public LineStation addLineStation(final LineStation lineStation) {
        LineStation validLineStation = validation(lineStation);
        LineStation station = validLineStation.addLineStation(lineStation);
        this.lineStations.add(station);
        return station;
    }

    public boolean isContains(final LineStation lineStation) {
        return this.lineStations.contains(lineStation);
    }

    public LineStations getLineStationBySorted() {
        List<LineStation> result = new ArrayList<>();
        LineStation startStation = findStartStation();
        result.add(startStation);
        insertLineStationBySorted(result, startStation);
        return new LineStations(result);
    }

    private void insertLineStationBySorted(List<LineStation> result, LineStation startStation) {
        Optional<LineStation> preStation = findPreStation(startStation.getCurrentStation());
        while (preStation.isPresent()) {
            LineStation station = preStation.get();
            result.add(station);
            preStation = findPreStation(station.getCurrentStation());
        }
    }

    private LineStation validation(final LineStation lineStation) {
        Optional<LineStation> isPreStation = findCurrentStation(lineStation.getPreStation());
        Optional<LineStation> isCurrentStation = findCurrentStation(lineStation.getCurrentStation());
        if (Objects.equals(isPreStation.isPresent(), isCurrentStation.isPresent())) {
            throw new IllegalArgumentException("invalid Argument");
        }
        return isPreStation.orElseGet(isCurrentStation::get);
    }

    private LineStation findStartStation() {
        return this.lineStations.stream().filter(LineStation::isStartStation).findFirst().orElseThrow(EntityExistsException::new);
    }

    private Optional<LineStation> findPreStation(final Station station) {
        return this.lineStations.stream()
                .filter(savedLineStation -> savedLineStation.isPreStation(station))
                .findFirst();
    }

    private Optional<LineStation> findCurrentStation(final Station station) {
        return this.lineStations.stream()
                .filter(savedLineStation -> savedLineStation.isCurrentStation(station))
                .findFirst();
    }

    private void validation(List<LineStation> lineStations) {
        if (Objects.isNull(lineStations)) {
            throw new IllegalArgumentException("invalid argument");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineStations that = (LineStations) o;
        return Objects.equals(lineStations, that.lineStations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineStations);
    }
}
