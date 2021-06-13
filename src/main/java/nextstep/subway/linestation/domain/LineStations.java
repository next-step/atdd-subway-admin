package nextstep.subway.linestation.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import nextstep.subway.NotFoundException;
import nextstep.subway.linestation.exception.DuplicateLineStationException;
import nextstep.subway.station.domain.Station;

@Embeddable
public class LineStations {

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final List<LineStation> lineStations = new ArrayList<>();

    public void addLineStation(final LineStation lineStation) {
        if (lineStations.contains(lineStation)) {
            throw new DuplicateLineStationException();
        }

        lineStations.add(lineStation);
    }

    public boolean contains(final LineStation lineStation) {
        return lineStations.contains(lineStation);
    }

    public void addLineStation(final Station upStation, final Station downStation, final int distance) {
        final List<Station> stations = Arrays.asList(upStation, downStation);
        validateStations(stations);
        final LineStation lineStation = findLineStation(stations);
        final LineStation newLineStation = makeLineStation(upStation, downStation, distance, lineStation);
        updateExistingLineStation(upStation, downStation, distance, lineStation);
        this.lineStations.add(newLineStation);
    }

    private void validateStations(final List<Station> stations) {
        if (countStations(stations) != 1) {
            throw new IllegalArgumentException();
        }
    }

    public int countStations(final List<Station> stations) {
        return (int)stations.stream()
            .filter(this::containsStation)
            .count();
    }

    public boolean containsStation(final Station station) {
        return lineStations.stream()
            .anyMatch(lineStation -> lineStation.isSameStation(station));
    }

    private LineStation findLineStation(final List<Station> stations) {
        return stations.stream()
            .filter(this::containsStation)
            .findFirst()
            .map(this::lineStation)
            .orElseThrow(NotFoundException::new);
    }

    public LineStation lineStation(final Station station) {
        return lineStations.stream()
            .filter(lineStation -> lineStation.isSameStation(station))
            .findFirst()
            .orElseThrow(NotFoundException::new);
    }

    private LineStation makeLineStation(final Station upStation, final Station downStation, final int distance,
        final LineStation lineStation) {

        if (lineStation.isSameStation(upStation)) {
            return initializeLineStation(downStation, distance, lineStation);
        }

        final LineStation newLineStation = new LineStation(lineStation.getLine(), upStation);
        newLineStation.next(lineStation.getStation(), distance);
        if (lineStation.getPreviousStation() != null && lineStation.getPreviousDistance() != null) {
            newLineStation.previous(lineStation.getPreviousStation(), lineStation.getPreviousDistance() - distance);
        }

        return newLineStation;
    }

    private LineStation initializeLineStation(final Station downStation, final int distance,
        final LineStation lineStation) {

        final LineStation newLineStation = new LineStation(lineStation.getLine(), downStation);
        newLineStation.previous(lineStation.getStation(), distance);
        if (lineStation.getNextStation() != null && lineStation.getNextDistance() != null) {
            newLineStation.next(lineStation.getNextStation(), lineStation.getNextDistance() - distance);
        }

        return newLineStation;
    }

    private void updateExistingLineStation(final Station upStation, final Station downStation, final int distance,
        final LineStation lineStation) {

        final List<LineStation> lineStations = lineStations(lineStation);
        lineStations.forEach(ls -> ls.update(upStation, downStation, distance));
    }

    private List<LineStation> lineStations(final LineStation lineStation) {
        final Predicate<LineStation> condition1 = ls -> ls.isSameStation(lineStation.getStation());
        final Predicate<LineStation> condition2 = ls -> ls.isSameStation(lineStation.getPreviousStation());
        final Predicate<LineStation> condition3 = ls -> ls.isSameStation(lineStation.getNextStation());

        return this.lineStations.stream()
            .filter(condition1.or(condition2).or(condition3))
            .collect(Collectors.toList());
    }

    public List<Station> orderedStations() {
        Optional<LineStation> optLIneStation = lineStations.stream()
            .filter(lineStation -> lineStation.getPreviousStation() == null)
            .findFirst();

        final List<Station> stations = new ArrayList<>();
        while (optLIneStation.isPresent()) {
            final Station station = optLIneStation.get()
                .getStation();
            stations.add(station);

            optLIneStation = lineStations.stream()
                .filter(ls -> ls.getPreviousStation() == station)
                .findFirst();
        }

        return stations;
    }
}
