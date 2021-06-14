package nextstep.subway.linestation.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import nextstep.subway.NotFoundException;
import nextstep.subway.linestation.exception.DuplicateLineStationException;
import nextstep.subway.linestation.exception.MethodNotAllowedException;
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

    public void addLineStation(final LineStation upStation, final LineStation downStation, final int distance) {
        final List<LineStation> stations = Arrays.asList(upStation, downStation);
        validateStations(stations);
        final LineStation persistedLineStation = persistedLineStation(findLineStation(stations));
        final LineStation newLineStation = makeLineStation(upStation, downStation, distance, persistedLineStation);
        updateExistingLineStation(upStation, downStation, distance, persistedLineStation);
        this.lineStations.add(newLineStation);
    }

    private LineStation persistedLineStation(final LineStation lineStation) {
        return lineStations.stream()
            .filter(ls -> ls.equals(lineStation))
            .findFirst()
            .orElseThrow(NotFoundException::new);
    }

    private void validateStations(final List<LineStation> stations) {
        if (countStations(stations) != 1) {
            throw new IllegalArgumentException();
        }
    }

    public int countStations(final List<LineStation> stations) {
        return (int)stations.stream()
            .filter(this::containsStation)
            .count();
    }

    public boolean containsStation(final LineStation lineStation) {
        return lineStations.stream()
            .anyMatch(ls -> ls.equals(lineStation));
    }

    private LineStation findLineStation(final List<LineStation> stations) {
        return stations.stream()
            .filter(this::containsStation)
            .findFirst()
            .orElseThrow(NotFoundException::new);
    }

    private LineStation makeLineStation(final LineStation upStation, final LineStation downStation, final int distance,
        final LineStation lineStation) {

        if (lineStation.equals(upStation)) {
            return initializeLineStation(downStation, distance, lineStation);
        }

        upStation.next(lineStation, distance);
        lineStation.getPreviousStation().ifPresent(
            ls -> upStation.previous(lineStation.getPreviousStation().get(),
                lineStation.getPreviousDistance().orElse(0) - distance)
        );

        return upStation;
    }

    private LineStation initializeLineStation(final LineStation downStation, final int distance,
        final LineStation lineStation) {

        downStation.previous(lineStation, distance);
        if (lineStation.getNextStation().isPresent()) {
            downStation.next(lineStation.getNextStation().get(), lineStation.getNextDistance().orElse(0) - distance);
        }

        return downStation;
    }

    private void updateExistingLineStation(final LineStation upStation, final LineStation downStation,
        final int distance, final LineStation lineStation) {

        lineStation.update(upStation, downStation, distance);
    }

    public List<Station> orderedStations() {
        Optional<LineStation> optLIneStation = lineStations.stream()
            .filter(lineStation -> !lineStation.getPreviousStation().isPresent())
            .findFirst();

        final List<Station> stations = new ArrayList<>();
        while (optLIneStation.isPresent()) {
            final LineStation lineStation = optLIneStation.get();
            final Station station = lineStation.getStation();
            stations.add(station);

            optLIneStation = lineStation.getNextStation();
        }

        return stations;
    }

    public void removeStation(final LineStation lineStation) {
        validateLineStationsSize();
        final LineStation currentStation = persistedLineStation(lineStation);
        currentStation.mergePrevAndNext();
        lineStations.remove(currentStation);
    }

    private void validateLineStationsSize() {
        if (lineStations.size() == 2) {
            throw new MethodNotAllowedException();
        }
    }
}
