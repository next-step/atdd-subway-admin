package nextstep.subway.domain;

import nextstep.subway.application.exception.exception.NotFoundDataException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

import static nextstep.subway.application.exception.type.AlreadyExceptionType.ALREADY_LINE_STATION;
import static nextstep.subway.application.exception.type.LineStationExceptionType.NOT_FOUND_LINE_STATION_BOTH;

@Embeddable
public class LineStations {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    public List<LineStation> lineStations = new ArrayList<>();

    protected LineStations() {
    }

    public List<Station> getStations() {
        return lineStations.stream()
                .map(LineStation::getRelationStation)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public void addLineStation(LineStation station) {
        if (lineStations.isEmpty()) {
            lineStations.add(station);
            return;
        }
        checkAlreadyExistStation(station);
        checkExistBothStation(station);

        lineStations.forEach(lineStation -> lineStation.update(station));
        lineStations.add(station);
    }

    private void checkAlreadyExistStation(LineStation station) {
        if (getStations().containsAll(station.getRelationStation())) {
            throw new NotFoundDataException(ALREADY_LINE_STATION.getMessage());
        }
    }

    private void checkExistBothStation(LineStation station) {
        if (isExistStations(station)) {
            throw new NotFoundDataException(NOT_FOUND_LINE_STATION_BOTH.getMessage());
        }
    }

    private boolean isExistStations(LineStation station) {
        List<Station> stations = getStations();
        return station.getRelationStation()
                .stream()
                .noneMatch(stations::contains);
    }
}
