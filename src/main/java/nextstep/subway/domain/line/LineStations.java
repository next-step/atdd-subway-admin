package nextstep.subway.domain.line;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import nextstep.subway.constants.ErrorMessage;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRegisterStatus;

@Embeddable
public class LineStations {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LineStation> lineStations = new ArrayList<>();

    public LineStations() {
    }

    public LineStations(List<LineStation> lineStations) {
        this.lineStations.addAll(lineStations);
    }

    public void add(LineStation lineStation) {
        this.lineStations.add(lineStation);
    }

    public void addAll(List<LineStation> lineStations) {
        this.lineStations.addAll(lineStations);
    }

    public boolean isEmpty() {
        return lineStations.isEmpty();
    }

    public Stream<LineStation> stream() {
        return lineStations.stream();
    }

    public StationRegisterStatus getStationRegisterStatus(Station station) {
        StationRegisterStatus stationRegisterStatus = new StationRegisterStatus();
        this.lineStations
                .forEach(lineStation -> stationRegisterStatus.add(lineStation.checkStationStatusOf(station)));
        return stationRegisterStatus;
    }

    public void checkLineStationExist() {
        if (lineStations.isEmpty()) {
            throw new IllegalArgumentException(ErrorMessage.NOT_REGISTERED_LINE_STATION);
        }
    }

    public void deleteLineStation(Station station) {
        LineStations lineStationsToDelete = getLineStationToDelete(station);
        lineStationsToDelete.stream().forEach(lineStationToDelete -> lineStations.remove(lineStationToDelete));
        addReAssignedLineStation(lineStationsToDelete, station);
    }

    private LineStations getLineStationToDelete(Station station) {
        return new LineStations(lineStations.stream()
                .filter(lineStation -> lineStation.containStation(station))
                .collect(Collectors.toList()));
    }

    private void addReAssignedLineStation(LineStations lineStationsToDelete, Station station) {
        StationRegisterStatus leftStationsStatus = new StationRegisterStatus();
        lineStationsToDelete.stream()
                .map(lineStation -> lineStation.oppositeStationStatusOf(station))
                .forEach(leftStationsStatus::add);
        LineStation lineStationToAdd = leftStationsStatus.createLineStation();
        if (lineStationToAdd != null) {
            lineStations.add(lineStationToAdd);
        }
    }

    public List<LineStation> getLineStations() {
        return lineStations;
    }

    public boolean contains(LineStation lineStation) {
        return this.lineStations.contains(lineStation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LineStations that = (LineStations) o;
        return Objects.equals(lineStations, that.lineStations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineStations);
    }

}
