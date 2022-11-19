package nextstep.subway.domain;

import nextstep.subway.common.exception.NotFoundDataException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.common.type.AlreadyExceptionType.ALREADY_LINE_STATION;
import static nextstep.subway.common.type.LineStationExceptionType.NOT_FOUND_LINE_STATION_BOTH;

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


    public void addLineStation(Station upStation, Station downStation, int distance) {
        LineStation newStation = LineStation.of(upStation, downStation, distance);
        if (lineStations.isEmpty()) {
            lineStations.add(newStation);
            return;
        }

        checkAlreadyExistStation(upStation, downStation);
        checkExistBothStation(newStation);


        lineStations.forEach(lineStation -> lineStation.update(newStation));
        lineStations.add(newStation);
    }

    private void checkAlreadyExistStation(Station upStation, Station downStation) {
        if (isContainUpStation(upStation) && isContainDownStation(downStation)) {
            throw new NotFoundDataException(ALREADY_LINE_STATION.getMessage());
        }
    }

    private boolean isContainUpStation(Station upStation) {
        return getStations().contains(upStation);
    }

    private boolean isContainDownStation(Station downStation) {
        return this.getStations().contains(downStation);
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
