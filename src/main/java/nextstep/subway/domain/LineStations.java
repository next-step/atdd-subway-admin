package nextstep.subway.domain;

import nextstep.subway.application.exception.exception.NotFoundDataException;
import nextstep.subway.application.exception.exception.NotValidDataException;
import nextstep.subway.application.exception.type.ValidExceptionType;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

import static nextstep.subway.application.exception.type.AlreadyExceptionType.ALREADY_LINE_STATION;
import static nextstep.subway.application.exception.type.LineStationExceptionType.NOT_FOUND_LINE_STATION_BOTH;

@Embeddable
public class LineStations {

    private static final int MIN_LINE_STATIONS_SIZE = 1;

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

    public void delete(Station deleteStation) {
        checkMinLineStationSize();

        Optional<LineStation> upStation = getUpStation(deleteStation);
        Optional<LineStation> downStation = getDownStation(deleteStation);

        if (upStation.isPresent() && downStation.isPresent()) {
            LineStation upLineStation = upStation.get();
            LineStation downLineStation = downStation.get();
            deleteCenterLineStation(upLineStation, downLineStation);
            return;
        }

        deleteEndLineStation(upStation, downStation);
    }

    private void deleteCenterLineStation(LineStation upLineStation, LineStation downLineStation) {
        renewalLineStation(upLineStation, downLineStation);
        deleteExistLineStation(upLineStation, downLineStation);
    }

    private void deleteEndLineStation(Optional<LineStation> upStation, Optional<LineStation> downStation) {
        upStation.ifPresent(lineStation -> lineStations.remove(lineStation));
        downStation.ifPresent(lineStation -> lineStations.remove(lineStation));
    }

    private void deleteExistLineStation(LineStation upStation, LineStation downStation) {
        lineStations.remove(upStation);
        lineStations.remove(downStation);
    }

    private void renewalLineStation(LineStation upStation, LineStation downStation) {
        LineStation renewLineStation = upStation.renewal(downStation);
        lineStations.add(renewLineStation);
    }

    private Optional<LineStation> getUpStation(Station station) {
        return lineStations.stream()
                .filter(lineStation -> lineStation.isSameUpStation(station))
                .findFirst();
    }

    private Optional<LineStation> getDownStation(Station station) {
        return lineStations.stream()
                .filter(lineStation -> lineStation.isSameDownStation(station))
                .findFirst();
    }

    private void checkMinLineStationSize() {
        if (lineStations.size() <= MIN_LINE_STATIONS_SIZE) {
            throw new NotValidDataException(ValidExceptionType.MIN_LINE_STATIONS_SIZE.getMessage());
        }
    }
}
